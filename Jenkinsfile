pipeline {
    agent any

    environment {
        MVN_HOME = tool name: 'Maven 3.9', type: 'maven'
        JAVA_HOME = tool name: 'JDK 21', type: 'jdk'
        SONAR_TOKEN = credentials('SONAR_TOKEN')
        DOCKER_IMAGE = "limits-service:${env.BUILD_NUMBER}"
        ECS_CLUSTER = "limits-service-cluster"
        ECS_SERVICE_STAGING = "limits-service-staging"
        ECS_SERVICE_PROD = "limits-service-prod"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Setup') {
            steps {
                echo "JDK y Maven configurados"
                sh "${JAVA_HOME}/bin/java -version"
                sh "${MVN_HOME}/bin/mvn -version"
            }
        }

        stage('Build & Unit Tests') {
            steps {
                echo "Compilando y ejecutando tests"
                sh "${MVN_HOME}/bin/mvn clean install"
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco execPattern: '**/target/jacoco.exec'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarCloud') {
                    sh "${MVN_HOME}/bin/mvn sonar:sonar \
                        -Dsonar.projectKey=limits-service \
                        -Dsonar.organization=my-org \
                        -Dsonar.login=${SONAR_TOKEN}"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                withDockerRegistry([credentialsId: 'DOCKERHUB_CREDENTIALS', url: 'https://index.docker.io/v1/']) {
                    sh "docker push ${DOCKER_IMAGE}"
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                sh """
                    aws ecs update-service \
                        --cluster ${ECS_CLUSTER} \
                        --service ${ECS_SERVICE_STAGING} \
                        --force-new-deployment
                """
            }
        }

        stage('Integration Tests (Staging)') {
            steps {
                echo "Ejecutando tests de integración contra Staging"
                sh "${MVN_HOME}/bin/mvn verify -Pintegration-tests"
            }
        }

        stage('Approval for Production') {
            steps {
                input message: "Aprobar despliegue a Producción?", ok: "Deploy"
            }
        }

        stage('Deploy to Production') {
            steps {
                sh """
                    aws ecs update-service \
                        --cluster ${ECS_CLUSTER} \
                        --service ${ECS_SERVICE_PROD} \
                        --force-new-deployment
                """
            }
        }
    }

    post {
        always {
            echo "Pipeline finalizado"
        }
        failure {
            mail to: 'frank.valencia@correo.com',
                 subject: "Build fallida: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "Revisar Jenkins para más detalles: ${env.BUILD_URL}"
        }
    }
}