def getSas(def storage_account_name, def storage_container_name) {
    node ('master') {
        sh '''#!/bin/bash
                set -x
                EXP=$(date -u -d "180 minutes" '+%Y-%m-%dT%H:%M:00Z')
                NOW=$(date -u '+%Y-%m-%dT%H:%M:00Z')
                echo "Time now: $NOW \nExp time: $EXP"
                az login --service-principal -u ''' + AZURE_CLIENT_ID + ''' -p ''' + AZURE_CLIENT_SECRET + ''' -t ''' + AZURE_TENANT_ID + ''' -o table
                az account set -s ''' + AZURE_SUBSCRIPTION_ID + ''' -o none
                TOKEN=$(az storage container generate-sas --account-name ''' + storage_account_name + ''' --n ''' + storage_container_name + ''' --permissions lr --expiry $EXP --https-only)
                echo ${TOKEN} > ./sas
                echo "Generated SAS token: ${TOKEN}"
                az logout
                '''
        SAS = readFile("$env.WORKSPACE/sas").replace("\"", "")
        return SAS
    }
}
return this