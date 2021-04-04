def getContainerSasByLogin(def storage_account_name, def storage_container_name) {
    node ('master') {
        sh '''#!/bin/bash
                set -e
                EXP=$(date -u -d "180 minutes" '+%Y-%m-%dT%H:%M:00Z')
                NOW=$(date -u '+%Y-%m-%dT%H:%M:00Z')
                echo "Time now: $NOW \nExp time: $EXP"
                az login --service-principal -u ''' + AZURE_CLIENT_ID + ''' -p ''' + AZURE_CLIENT_SECRET + ''' -t ''' + AZURE_TENANT_ID + ''' -o table
                az account set -s ''' + AZURE_SUBSCRIPTION_ID + ''' -o none
                TOKEN=$(az storage container generate-sas --account-name ''' + storage_account_name + ''' --n ''' + storage_container_name + ''' --permissions lr --expiry $EXP --as-user --auth-mode login --https-only)
                echo ${TOKEN} > ./sas
                az logout
                '''
        SAS = readFile("$env.WORKSPACE/sas").replace("\"", "")
        if (SAS) {
            println 'SAS token generated successfully'
            return SAS
        } else {
            println 'SAS generation FAILED! Exiting ...'
            System.exit(1)
        }
    }
}

def getContainerSasByAccountKey(def storage_container_name) {
    node ('master') {
        sh '''#!/bin/bash
                set -e
                EXP=$(date -u -d "180 minutes" '+%Y-%m-%dT%H:%M:00Z')
                NOW=$(date -u '+%Y-%m-%dT%H:%M:00Z')
                echo "Time now: $NOW \nExp time: $EXP"
                TOKEN=$(az storage container generate-sas --account-name ''' + AZURE_STORAGE_ACCOUNT_NAME + ''' --account-key ''' + AZURE_STORAGE_ACCOUNT_KEY + ''' --n ''' + storage_container_name + ''' --permissions lr --expiry $EXP --https-only)
                echo ${TOKEN} > ./sas
                '''
        SAS = readFile("$env.WORKSPACE/sas").replace("\"", "")
        if (SAS) {
            println 'SAS token generated successfully'
            return SAS
        } else {
            println 'SAS generation FAILED! Exiting ...'
            System.exit(1)
        }
    }
}

return this