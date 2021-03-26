def getSas(def storage_account_name, def storage_container_name) {
    node ('master') {
        println( "Account:" + storage_account_name + "\nBLOB:" + storage_container_name)
    }
}