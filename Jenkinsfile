pipeline {
    agent any

    stages {
        stage('Build & Deploy') {
            when {
                expression {
                    echo "BRANCH_NAME: ${env.BRANCH_NAME}"
                    return env.BRANCH_NAME == 'develop'
                }
            }
            steps {
                echo '✅ 이 메시지가 출력되면 sh 명령어 실행 가능 상태입니다'

                // 나머지 생략...
            }
        }
    }
}
