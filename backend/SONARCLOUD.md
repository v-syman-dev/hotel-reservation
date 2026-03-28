./mvnw -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=YOUR_PROJECT_KEY \
  -Dsonar.organization=YOUR_ORGANIZATION_KEY \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token=YOUR_SONAR_TOKEN \
  -Dsonar.coverage.exclusions=**/dto/**/*.java,**/config/**/*.java,**/controller/**/*.java,**/mapper/**/*.java,**/aspect/**/*.java,**/handler/**,**/HotelreservationApplication.java,**/*Exception.java
