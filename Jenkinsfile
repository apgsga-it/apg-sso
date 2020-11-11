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

        stage("build keycloak") {
            steps {
			    withMaven(
			        maven: 'apache-maven-3.6.3'
			    ) {
                    echo "Compile KeyCloak producing Java Image Builder File"
                    sh "( whoami && pwd && cd keycloak && mvn clean verify -DskipTests -Ptar-build)"
			    }           
                echo "Build Docker Image from Java Image Builder File and Push to Docker Registry"
                sh "( scp -B -o StrictHostKeyChecking=no keycloak/target/jib-image.tar dockerbuild-dev@lxpwi072.apgsga.ch:/var/opt/apg-keycloak/ )"
                sh "( ssh -o StrictHostKeyChecking=no dockerbuild-dev@lxpwi072.apgsga.ch ls -al /var/opt/apg-keycloak/ )"
                sh "( ssh -o StrictHostKeyChecking=no dockerbuild-dev@lxpwi072.apgsga.ch docker load -i /var/opt/apg-keycloak/jib-image.tar )"
            }
        }
        stage("deploy") {
            steps {
                echo "Deploy latest locally published Docker Image to Docker Registry /apgsga/keycloak:dev"
                sh "( ssh -o StrictHostKeyChecking=no dockerbuild-dev@lxpwi072.apgsga.ch docker rmi dockerregistry.apgsga.ch:5000/apgsga/keycloak:dev -f )"
                sh "( ssh -o StrictHostKeyChecking=no dockerbuild-dev@lxpwi072.apgsga.ch docker tag apg-sso/keycloak dockerregistry.apgsga.ch:5000/apgsga/keycloak:dev )"
                sh "( ssh -o StrictHostKeyChecking=no dockerbuild-dev@lxpwi072.apgsga.ch docker push dockerregistry.apgsga.ch:5000/apgsga/keycloak:dev )"
            }
        }
    }
}
