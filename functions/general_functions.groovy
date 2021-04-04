import java.time.Instant
import java.time.Duration

def getContainerSas(def storage_account_name, def storage_container_name) {
    node ('master') {
        def sas_start = Instant.now()
        def sas_end = sas_start.plus(Duration.ofHours(3))
        println('Token would be valid from ' + sas_start + ' till '+ sas_end)
        sh '''#!/bin/bash
                set -e
                az login --service-principal -u ''' + AZURE_CLIENT_ID + ''' -p ''' + AZURE_CLIENT_SECRET + ''' -t ''' + AZURE_TENANT_ID + ''' -o none
                az account set -s ''' + AZURE_SUBSCRIPTION_ID + ''' -o none
                TOKEN=$(az storage container generate-sas --account-name ''' + storage_account_name + ''' --n ''' + storage_container_name + ''' --expiry ''' + sas_end + ''' --permissions lr --https-only  --as-user --auth-mode login)
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
def getClassicContainerSas (def storage_container_name) {
    node ('master') {
        def sas_start = Instant.now()
        def sas_end = sas_start.plus(Duration.ofHours(3))
        println('Token would be valid from ' + sas_start + ' till '+ sas_end)
        sh '''#!/bin/bash
                set -e
                TOKEN=$(az storage container generate-sas --account-name ''' + AZURE_STORAGE_ACCOUNT_NAME + ''' --account-key ''' + AZURE_STORAGE_ACCOUNT_KEY + ''' --n ''' + storage_container_name + ''' --expiry ''' + sas_end + ''' --permissions lr --https-only)
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