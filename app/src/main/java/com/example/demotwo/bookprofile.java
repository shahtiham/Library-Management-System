package com.example.demotwo;

public class bookprofile {
    public String bookname, authorname;
    public bookprofile() { }

    public bookprofile(String bookname, String authorname) {
        this.bookname = bookname;
        this.authorname = authorname;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }
}
