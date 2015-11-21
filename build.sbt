name := "ComputerVision"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
	"org.slf4j"                   % "slf4j-api"           % "1.7.5",
	"org.slf4j"                   % "slf4j-simple"        % "1.7.5",
	"org.clapper"                %% "grizzled-slf4j"      % "1.0.2",
	"com.typesafe"                % "config"              % "1.3.0",
	"org.json"                    % "json"                % "20150729",
  "org.scalanlp" %% "breeze" % "0.11.2",
  "org.scalanlp" %% "breeze-natives" % "0.11.2",
  "org.scalanlp" %% "breeze-viz" % "0.11.2"
)

resolvers ++= Seq(
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)
