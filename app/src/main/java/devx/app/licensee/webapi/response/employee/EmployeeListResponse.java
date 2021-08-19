package devx.app.licensee.webapi.response.employee;

import java.util.ArrayList;

public class EmployeeListResponse {
    private String message;
    private boolean success;
    private ArrayList<EmployeeDetailsObj> mArrEmployeeList;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ArrayList<EmployeeDetailsObj> getmArrEmployeeList() {
        return mArrEmployeeList;
    }

    public void setmArrEmployeeList(ArrayList<EmployeeDetailsObj> mArrEmployeeList) {
        this.mArrEmployeeList = mArrEmployeeList;
    }
}
