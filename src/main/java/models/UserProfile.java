package models;

public class UserProfile {
    private int userID;
    private String fullName;
    private String jobTitle;
    private String bio;
    private String location;
    private String exp;
    private String website;
    private String facebookLink;
    private String discordLink;
    private String portfolioLink;
    private String coverPicture;

    public UserProfile() {}

    // constructor for all object:))
    public UserProfile(int userID, String fullName, String jobTitle, String bio, String location,
                       String exp, String website, String facebookLink, String discordLink, String portfolioLink,
                       String coverPicture) {
        this.userID = userID;
        this.fullName = fullName;
        this.jobTitle = jobTitle;
        this.bio = bio;
        this.location = location;
        this.exp = exp;
        this.website = website;
        this.facebookLink = facebookLink;
        this.discordLink = discordLink;
        this.portfolioLink = portfolioLink;
        this.coverPicture = coverPicture;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBio() {
        return jobTitle;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getExp() {
        return exp;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setDiscordLink(String discordLink) {
        this.discordLink = discordLink;
    }

    public String getDiscordLink() {
        return discordLink;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setPortfolioLink(String portfolioLink) {
        this.portfolioLink = portfolioLink;
    }

    public String getPortfolioLink() {
        return portfolioLink;
    }

//    @Override
//    public String toString() {
//        return "User{" +
//                "userId=" + userId +
//                ", username='" + username + '\'' +
//                ", email='" + email + '\'' +
//                ", status=" + status +
//                ", lastSeen=" + lastSeen +
//                '}';
//    }
}
