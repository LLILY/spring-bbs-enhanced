package app.models;

/**
 * Created by lili19289 on 2016/8/4.
 */
public class User extends BaseModel{

    public String user_name;

    public String password;

    public int age;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
