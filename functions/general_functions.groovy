def get_sas(def storage_account_name, def storage_container_name) {
    node ('master') {
        echo "Account: ${storage_account_name} \n BLOB: ${storage_container_name}"
    }
}