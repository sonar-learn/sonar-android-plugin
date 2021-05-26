/*
 * SonarQube Android Lint Plugin
 * Copyright (C) 2013-2016 SonarSource SA and Jerome Van Der Linden, Stephane Nicolas, Florian Roncari, Thomas Bores
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plugins.android.lint;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssueLocation;
import org.sonar.api.rule.RuleKey;

import java.io.File;
import java.util.List;

public class AndroidLintProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AndroidLintProcessor.class);
  private final SensorContext context;

  public AndroidLintProcessor(SensorContext context) {
    this.context = context;
  }

  public void process(File lintXml) {
    Serializer serializer = new Persister();
    try {
      LOGGER.info("Processing android lint report: "+lintXml.getPath());
      LintIssues lintIssues = serializer.read(LintIssues.class, lintXml);
      for (LintIssue lintIssue : lintIssues.issues) {
        processIssue(lintIssue);
      }
    } catch (Exception e) {
      LOGGER.error("Exception reading " + lintXml.getPath(), e);
    }
  }

  private void processIssue(LintIssue lintIssue) {
    LOGGER.debug("Processing Issue: {}", lintIssue.id);
    for (LintLocation lintLocation : lintIssue.locations) {
      processIssueForLocation(lintIssue, lintLocation);
    }
  }

  private void processIssueForLocation(LintIssue lintIssue, LintLocation lintLocation) {
    InputFile inputFile = this.context.fileSystem().inputFile(this.context.fileSystem().predicates().hasPath(lintLocation.file));
    if (inputFile != null) {
      LOGGER.debug("Processing File {} for Issue {}", lintLocation.file, lintIssue.id);
      DefaultIssueLocation dil = new DefaultIssueLocation()
              .on(inputFile)
              .at(inputFile.selectLine(lintLocation.line != null ? lintLocation.line : 1))
              .message(lintIssue.message);
      this.context.newIssue()
              .forRule(RuleKey.of(AndroidLintRulesDefinition.REPOSITORY_KEY, lintIssue.id))
              .at(dil)
              .save();
      return;
    }
    LOGGER.warn("Unable to find file {} to report issue", lintLocation.file);
  }

  @Root(name = "location", strict = false)
  private static class LintLocation {
    @Attribute
    String file;
    @Attribute(required = false)
    Integer line;
  }

  @Root(name = "issues", strict = false)
  private static class LintIssues {
    @ElementList(required = false, inline = true, empty = false)
    List<LintIssue> issues;
  }

  @Root(name = "issue", strict = false)
  private static class LintIssue {
    @Attribute
    String id;
    @Attribute
    String message;
    @ElementList(inline = true)
    List<LintLocation> locations;
  }

}
