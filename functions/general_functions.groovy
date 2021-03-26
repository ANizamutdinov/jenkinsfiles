def get_sas(storage_account_name, storage_container_name) {
    node ('master') {
        echo "Account: ${storage_account_name} \n BLOB: ${storage_container_name}"
    }
}