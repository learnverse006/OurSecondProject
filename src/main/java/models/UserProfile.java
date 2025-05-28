package models;

public class UserProfile {
    private int userId;
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
    private String avatarPicture;
    public UserProfile() {}

    // constructor for all object:))
    public UserProfile(int userId, String fullName, String jobTitle, String bio, String location, String exp,
                       String website, String facebookLink, String discordLink, String portfolioLink, String coverPicture,
                       String avatarPicture) {
        this.userId = userId;
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
        this.avatarPicture = avatarPicture;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public void setDiscordLink(String discordLink) {
        this.discordLink = discordLink;
    }

    public void setPortfolioLink(String portfolioLink) {
        this.portfolioLink = portfolioLink;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    public int getUserId() {
        return userId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getExp() {
        return exp;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public String getDiscordLink() {
        return discordLink;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPortfolioLink() {
        return portfolioLink;
    }

    public String getWebsite() {
        return website;
    }

    public void setAvatarPicture(String avatarPicture) {
        this.avatarPicture = avatarPicture;
    }

    public String getAvatarPicture() {
        return avatarPicture;
    }
    @Override
    public String toString() {
        return super.toString();
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
