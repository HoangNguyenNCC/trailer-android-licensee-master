package devx.app.licensee.modules.inviteEmployees

data class PermissionItem(val atribute:String ,
                          var permissionArr: ArrayList<String>,
                          var isPermissionSelected: Int, var selectedPermissionArr:ArrayList<String>)