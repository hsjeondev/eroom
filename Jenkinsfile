pipeline {
    agent any

    stages {
        stage('Build & Deploy') {
            when {
                branch 'develop'
            }
            steps {
                echo '배포: develop 서버로'

                sh 'cp /mnt/env/.env ./springboot-docker/.env'

                sh 'mkdir -p ./springboot-docker/secrets'
                sh 'cp /mnt/env/secrets/application.yml ./springboot-docker/secrets/'
                sh 'cp /mnt/env/secrets/application-secret.properties ./springboot-docker/secrets/'

                sh './springboot-docker/deploy-dev.sh'
            }
        }

        stage('Build & Deploy Prod') {
            when {
                branch 'master'
            }
            steps {
                echo '배포: production 서버로'
                sh './deploy-prod.sh'
            }
        }
    }
}
