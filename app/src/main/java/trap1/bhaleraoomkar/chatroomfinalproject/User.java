package trap1.bhaleraoomkar.chatroomfinalproject;



public class User implements Comparable<User> {
    private String username;
    private double latitude;
    private double longitude;
    private String city;
    private String country;

    public User(String a, double b, double c, String city, String country){
        username = a;
        latitude = b;
        longitude = c;
        this.city = city;
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUsername() {
        return username;
    }

    public String getCity(){return city;}

    public String getCountry(){return country;}

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int compareTo(User u){
        return username.toString().compareTo(u.username.toString());
    }
}
