pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: "15"))
        timeout(time: 60, unit: "MINUTES")
        timestamps()
    }

    stages {
        stage("info") {
            steps {
                sh "java -version"

                echo "Node Name: ${env.NODE_NAME}"
                echo "Executor Number: ${env.EXECUTOR_NUMBER}"

                echo "Source Branch: ${env.BRANCH_NAME}"
            }
        }

    	stage("checkout") {
    		steps {
                script {
                    sh "( cvs -d:ext:svcCvsClient@cvs-t.apgsga.ch:/var/local/cvs/root co apg-sso )"
                }
    		}
    	}

        stage("build keycloak") {
            steps {
			    withMaven(
			        maven: 'apache-maven-3.6.3'
			    ) {
                    sh "( cd apg-sso/keycloak && chmod +x mvnw && mvn clean verify -DskipTests -Ptar-build)"
			    }           
            }
        }
    }

}
