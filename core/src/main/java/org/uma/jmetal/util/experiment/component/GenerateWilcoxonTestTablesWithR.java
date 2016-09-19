//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentComponent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class generates a R script that computes the Wilcoxon Signed Rank Test and generates a Latex script
 * that produces a table per quality indicator containing the pairwise comparison between all the algorithms
 * on all the solved problems.
 * <p>
 * The results are a set of R files that are written in the directory
 * {@link Experiment #getExperimentBaseDirectory()}/R. Each file is called as
 * indicatorName.Wilcoxon.R
 * <p>
 * To run the R script: Rscript indicatorName.Wilcoxon.R
 * To generate the resulting Latex file: pdflatex indicatorName.Wilcoxon.tex
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateWilcoxonTestTablesWithR<Result> implements ExperimentComponent {
    private static final String DEFAULT_R_DIRECTORY = "R";

    private final Experiment<?, Result> experiment;

    public GenerateWilcoxonTestTablesWithR(Experiment<?, Result> experimentConfiguration) {
        this.experiment = experimentConfiguration;
        this.experiment.removeDuplicatedAlgorithms();

        experiment.removeDuplicatedAlgorithms();
    }

    @Override
    public void run() throws IOException {
        String rDirectoryName = experiment.getExperimentBaseDirectory() + "/" + DEFAULT_R_DIRECTORY;
        File rOutput;
        rOutput = new File(rDirectoryName);
        if (!rOutput.exists()) {
            boolean result = new File(rDirectoryName).mkdirs();
            System.out.println("Creating " + rDirectoryName + " directory");
        }
        for (GenericIndicator<? extends Solution<?>> indicator : experiment.getIndicatorList()) {
            String rFileName = rDirectoryName + "/" + indicator.getName() + ".Wilcoxon" + ".R";
            String latexFileName = rDirectoryName + "/" + indicator.getName() + ".Wilcoxon" + ".tex";

            printHeaderLatexCommands(rFileName, latexFileName);
            printTableHeader(indicator, rFileName, latexFileName);
            printLines(indicator, rFileName, latexFileName);
            printTableTail(rFileName, latexFileName);
            printEndLatexCommands(rFileName, latexFileName);

            printGenerateMainScript(indicator, rFileName, latexFileName);
        }
    }

    private void printHeaderLatexCommands(String rFileName, String latexFileName) throws IOException {
        FileWriter os = new FileWriter(rFileName, false);
        String output = "write(\"\", \"" + latexFileName + "\",append=FALSE)";
        os.write(output + "\n");

        String dataDirectory = experiment.getExperimentBaseDirectory() + "/data";
        os.write("resultDirectory<-\"" + dataDirectory + "\"" + "\n");
        output = "latexHeader <- function() {" + "\n" +
                "  write(\"\\\\documentclass{article}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\title{StandardStudy}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\usepackage{amssymb}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\author{A.J.Nebro}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\begin{document}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\maketitle\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\section{Tables}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\\", \"" + latexFileName + "\", append=TRUE)" + "\n" + "}" + "\n";
        os.write(output + "\n");

        os.close();
    }

    private void printEndLatexCommands(String rFileName, String latexFileName) throws IOException {
        FileWriter os = new FileWriter(rFileName, true);
        String output = "latexTail <- function() { " + "\n" +
                "  write(\"\\\\end{document}\", \"" + latexFileName + "\", append=TRUE)" + "\n" + "}" + "\n";
        os.write(output + "\n");
        os.close();
    }

    private void printTableHeader(GenericIndicator<?> indicator, String rFileName, String latexFileName) throws IOException {
        FileWriter os = new FileWriter(rFileName, true);

        String caption = indicator.getName() + ". Problems: ";
        for (Problem<?> problem : experiment.getProblemList()) {
            caption += problem.getName() + " ";
        }

        String latexTableLabel = "";
        String latexTableCaption = "";

        // Write function latexTableHeader
        latexTableCaption = "  write(\"\\\\caption{\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(problem, \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"." + indicator.getName() + ".}\", \"" + latexFileName + "\", append=TRUE)" + "\n";
        latexTableLabel = "  write(\"\\\\label{Table:\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(problem, \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"." + indicator.getName() + ".}\", \"" + latexFileName + "\", append=TRUE)" + "\n";

        // Generate function latexTableHeader()
        String output = "latexTableHeader <- function(problem, tabularString, latexTableFirstLine) {" + "\n" +
                "  write(\"\\\\begin{table}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                latexTableCaption + "\n" +
                latexTableLabel + "\n" +
                "  write(\"\\\\centering\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\begin{scriptsize}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                //"  write(\"\\\\begin{tabular}{" + latexTabularAlignment + "}\", \"" + texFile + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\begin{tabular}{\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(tabularString, \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                //latexTableFirstLine +
                "  write(latexTableFirstLine, \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\hline \", \"" + latexFileName + "\", append=TRUE)" + "\n" + "}" + "\n";
        os.write(output + "\n");
        os.close();
    }

    private void printTableTail(String rFileName, String latexFileName) throws IOException {
        // Generate function latexTableTail()
        FileWriter os = new FileWriter(rFileName, true);

        String output = "latexTableTail <- function() { " + "\n" +
                "  write(\"\\\\hline\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\end{tabular}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\end{scriptsize}\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "  write(\"\\\\end{table}\", \"" + latexFileName + "\", append=TRUE)" + "\n" + "}" + "\n";
        os.write(output + "\n");

        os.close();
    }

    private void printLines(GenericIndicator<?> indicator, String rFileName, String latexFileName) throws IOException {
        FileWriter os = new FileWriter(rFileName, true);

        String output;
        if (indicator.isTheLowerTheIndicatorValueTheBetter()) {
            output = "printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { " + "\n" +
                    "  file1<-paste(resultDirectory, algorithm1, sep=\"/\")" + "\n" +
                    "  file1<-paste(file1, problem, sep=\"/\")" + "\n" +
                    "  file1<-paste(file1, indicator, sep=\"/\")" + "\n" +
                    "  data1<-scan(file1)" + "\n" +
                    "  file2<-paste(resultDirectory, algorithm2, sep=\"/\")" + "\n" +
                    "  file2<-paste(file2, problem, sep=\"/\")" + "\n" +
                    "  file2<-paste(file2, indicator, sep=\"/\")" + "\n" +
                    "  data2<-scan(file2)" + "\n" +
                    "  if (i == j) {" + "\n" +
                    "    write(\"-- \", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                    "  }" + "\n" +
                    "  else if (i < j) {" + "\n" +
                    "    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {" + "\n" +
                    "      if (median(data1) <= median(data2)) {" + "\n" +
                    "        write(\"$\\\\blacktriangle$\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                    "      }" + "\n" +
                    "      else {" + "\n" +
                    "        write(\"$\\\\triangledown$\", \"" + latexFileName + "\", append=TRUE) " + "\n" +
                    "      }" + "\n" +
                    "    }" + "\n" +
                    "    else {" + "\n" +
                    "      write(\"--\", \"" + latexFileName + "\", append=TRUE) " + "\n" +
                    "    }" + "\n" +
                    "  }" + "\n" +
                    "  else {" + "\n" +
                    "    write(\" \", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                    "  }" + "\n" +
                    "}" + "\n";

        } else {
            output = "printTableLine <- function(indicator, algorithm1, algorithm2, i, j, problem) { " + "\n" +
                    "  file1<-paste(resultDirectory, algorithm1, sep=\"/\")" + "\n" +
                    "  file1<-paste(file1, problem, sep=\"/\")" + "\n" +
                    "  file1<-paste(file1, indicator, sep=\"/\")" + "\n" +
                    "  data1<-scan(file1)" + "\n" +
                    "  file2<-paste(resultDirectory, algorithm2, sep=\"/\")" + "\n" +
                    "  file2<-paste(file2, problem, sep=\"/\")" + "\n" +
                    "  file2<-paste(file2, indicator, sep=\"/\")" + "\n" +
                    "  data2<-scan(file2)" + "\n" +
                    "  if (i == j) {" + "\n" +
                    "    write(\"--\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                    "  }" + "\n" +
                    "  else if (i < j) {" + "\n" +
                    "    if (is.finite(wilcox.test(data1, data2)$p.value) & wilcox.test(data1, data2)$p.value <= 0.05) {" + "\n" +
                    "      if (median(data1) >= median(data2)) {" + "\n" +
                    "        write(\"$\\\\blacktriangle$\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                    "      }" + "\n" +
                    "      else {" + "\n" +
                    "        write(\"$\\\\triangledown$\", \"" + latexFileName + "\", append=TRUE) " + "\n" +
                    "      }" + "\n" +
                    "    }" + "\n" +
                    "    else {" + "\n" +
                    "      write(\"$-$\", \"" + latexFileName + "\", append=TRUE) " + "\n" +
                    "    }" + "\n" +
                    "  }" + "\n" +
                    "  else {" + "\n" +
                    "    write(\" \", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                    "  }" + "\n" +
                    "}" + "\n";
        }
        os.write(output + "\n");
        os.close();
    }

    private void printGenerateMainScript(GenericIndicator<?> indicator, String rFileName, String latexFileName) throws IOException {
        FileWriter os = new FileWriter(rFileName, true);

        // Start of the R script
        String output = "### START OF SCRIPT ";
        os.write(output + "\n");

        String problemList = "problemList <-c(";
        String algorithmList = "algorithmList <-c(";

        for (int i = 0; i < (experiment.getProblemList().size() - 1); i++) {
            problemList += "\"" + experiment.getProblemList().get(i).getName() + "\", ";
        }
        problemList += "\"" + experiment.getProblemList().get(experiment.getProblemList().size() - 1).getName() + "\") ";

        for (int i = 0; i < (experiment.getAlgorithmList().size() - 1); i++) {
            algorithmList += "\"" + experiment.getAlgorithmList().get(i).getTag() + "\", ";
        }
        algorithmList += "\"" + experiment.getAlgorithmList().get(experiment.getAlgorithmList().size() - 1).getTag() + "\") ";

        String latexTabularAlignment = "l";
        for (int i = 1; i < experiment.getAlgorithmList().size(); i++) {
            latexTabularAlignment += "c";
        }

        latexTabularAlignment = "l";
        String latexTableFirstLine = "\\\\hline ";

        for (int i = 1; i < experiment.getAlgorithmList().size(); i++) {
            latexTabularAlignment += "c";
            latexTableFirstLine += " & " + experiment.getAlgorithmList().get(i).getTag();
        }
        latexTableFirstLine += "\\\\\\\\ \"";

        String tabularString = "tabularString <-c(" + "\"" + latexTabularAlignment + "\"" + ") ";
        String tableFirstLine = "latexTableFirstLine <-c(" + "\"" + latexTableFirstLine + ") ";

        output = "# Constants" + "\n" +
                problemList + "\n" +
                algorithmList + "\n" +
                tabularString + "\n" +
                tableFirstLine + "\n" +
                "indicator<-\"" + indicator.getName() + "\"";
        os.write(output + "\n");

        output = "\n # Step 1.  Writes the latex header" + "\n" +
                "latexHeader()";
        os.write(output + "\n");

        // Generate full table
        problemList = "";
        for (Problem<?> problem : experiment.getProblemList()) {
            problemList += problem.getName() + " ";
        }
        // The tabular environment and the latexTableFirstLine encodings.variable must be redefined
        latexTabularAlignment = "| l | ";
        latexTableFirstLine = "\\\\hline \\\\multicolumn{1}{|c|}{}";
        for (int i = 1; i < experiment.getAlgorithmList().size(); i++) {
            for (Problem<?> problem : experiment.getProblemList()) {
                latexTabularAlignment += "p{0.15cm }";
            }
            latexTableFirstLine += " & \\\\multicolumn{" + experiment.getProblemList().size() + "}{c|}{" + experiment.getAlgorithmList().get(i).getTag() + "}";
            latexTabularAlignment += " | ";
        }
        latexTableFirstLine += " \\\\\\\\";

        tabularString = "tabularString <-c(" + "\"" + latexTabularAlignment + "\"" + ") ";
        latexTableFirstLine = "latexTableFirstLine <-c(" + "\"" + latexTableFirstLine + "\"" + ") ";

        output = tabularString;
        os.write(output + "\n" + "\n");
        output = latexTableFirstLine;
        os.write(output + "\n" + "\n");

        output = "# Step 3. Problem loop " + "\n" +
                "latexTableHeader(\"" + problemList + "\", tabularString, latexTableFirstLine)" + "\n\n" +
                "indx = 0" + "\n" +
                "for (i in algorithmList) {" + "\n" +
                "  if (i != \"" + experiment.getAlgorithmList().get(experiment.getAlgorithmList().size() - 1).getTag() + "\") {" + "\n" +
                "    write(i , \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "    write(\" & \", \"" + latexFileName + "\", append=TRUE)" + "\n" + "\n" +
                "    jndx = 0" + "\n" +
                "    for (j in algorithmList) {" + "\n" +
                "      for (problem in problemList) {" + "\n" +
                "        if (jndx != 0) {" + "\n" +
                "          if (i != j) {" + "\n" +
                "            printTableLine(indicator, i, j, indx, jndx, problem)" + "\n" +
                "          }" + "\n" +
                "          else {" + "\n" +
                "            write(\"  \", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "          } " + "\n" +
                "          if (problem == \"" + experiment.getProblemList().get(experiment.getProblemList().size() - 1).getName() + "\") {" + "\n" +
                "            if (j == \"" + experiment.getAlgorithmList().get(experiment.getAlgorithmList().size() - 1).getTag() + "\") {" + "\n" +
                "              write(\" \\\\\\\\ \", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "            } " + "\n" +
                "            else {" + "\n" +
                "              write(\" & \", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "            }" + "\n" +
                "          }" + "\n" +
                "     else {" + "\n" +
                "    write(\"&\", \"" + latexFileName + "\", append=TRUE)" + "\n" +
                "     }" + "\n" +
                "        }" + "\n" +
                "      }" + "\n" +
                "      jndx = jndx + 1" + "\n" +
                "    }" + "\n" +
                "    indx = indx + 1" + "\n" +
                "  }" + "\n" +
                "} # for algorithm" + "\n" + "\n" +
                "  latexTableTail()" + "\n";

        os.write(output + "\n");

        // Generate end of file
        output = "#Step 3. Writes the end of latex file " + "\n" +
                "latexTail()" + "\n";
        os.write(output + "\n");

        os.close();
    }
}