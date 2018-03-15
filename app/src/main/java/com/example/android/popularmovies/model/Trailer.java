package com.example.android.popularmovies.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable {

    public static final Creator<Trailer> CREATOR = new Creator<Trailer>() {
        @Override
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
    private String id;
    private String iso;
    private String key;
    private String name;
    private int size;
    private String type;
    private String url;

    public Trailer(String id, String iso, String key, String name, String site, int size, String type) {
        this.id = id;
        this.iso = iso;
        this.key = key;
        this.name = name;
        this.size = size;
        this.type = type;
    }

    Trailer(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public Trailer(String name) {
        this.name = name;
    }

    protected Trailer(Parcel in) {
        id = in.readString();
        iso = in.readString();
        key = in.readString();
        name = in.readString();
        size = in.readInt();
        type = in.readString();
    }


    public String getId() {
        return id;
    }

    public String getIso() {
        return iso;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(iso);
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeInt(size);
        parcel.writeString(type);
    }
}
