package com.spgon.a3flowers.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DistrictManager {
    private static DistrictManager instance;
    private List<String> andhraPradeshDistricts;
    private List<String> telanganaDistricts;

    private DistrictManager() {
        // Initialize the district data
        andhraPradeshDistricts = new ArrayList<>();
        telanganaDistricts = new ArrayList<>();
    }

    public static DistrictManager getInstance() {
        if (instance == null) {
            instance = new DistrictManager();
        }
        return instance;
    }

    public List<String> getAndhraPradeshDistricts() {
        // You can load the district data here from your data source (e.g., JSON file, API, etc.)
        // For this example, we'll manually add some sample districts.
//        andhraPradeshDistricts.clear();
//        andhraPradeshDistricts.add("Srikakulam");
//        andhraPradeshDistricts.add("Parvathipuram Manyam");
//        andhraPradeshDistricts.add("Vizianagaram");
//        andhraPradeshDistricts.add("Visakhapatnam");
//        andhraPradeshDistricts.add("Alluri Sitharama Raju");
//        andhraPradeshDistricts.add("Anakapalli");
//        andhraPradeshDistricts.add("Kakinada");
//        andhraPradeshDistricts.add("East Godavari");
//        andhraPradeshDistricts.add("Dr. B. R. Ambedkar Konaseema");
//        andhraPradeshDistricts.add("Eluru");
//        andhraPradeshDistricts.add("West Godavari");
//        andhraPradeshDistricts.add("NTR");
//        andhraPradeshDistricts.add("Krishna");
//        andhraPradeshDistricts.add("Palnadu");
//        andhraPradeshDistricts.add("Guntur");
//        andhraPradeshDistricts.add("Bapatla");
//        andhraPradeshDistricts.add("Prakasam");
//        andhraPradeshDistricts.add("Sri Potti Sriramulu Nellore");
//        andhraPradeshDistricts.add("Kurnool");
//        andhraPradeshDistricts.add("Nandyal");
//        andhraPradeshDistricts.add("Anantapur");
//        andhraPradeshDistricts.add("Sri satya sai");
//        andhraPradeshDistricts.add("YSR");
//        andhraPradeshDistricts.add("Annamayya");
//        andhraPradeshDistricts.add("Tirupati");
//        andhraPradeshDistricts.add("chitoor");

        String[] dists = {"Anantapur",
                "Chittoor",
                "East Godavari",
                "Guntur",
                "Kadapa",
                "Krishna",
                "Kurnool",
                "Nellore",
                "Prakasam",
                "Srikakulam",
                "Visakhapatnam",
                "Vizianagaram",
                "West Godavari" };
        List<String> andhraPradeshDistricts = Arrays.asList(dists);
        // Add more districts as needed
        return andhraPradeshDistricts;
    }

    public List<String> getTelanganaDistricts() {
        // Similar to getAndhraPradeshDistricts, load the district data for Telangana here.
//        telanganaDistricts.clear();
//        telanganaDistricts.add("Adilabad");
//        telanganaDistricts.add("Kumuram Bheem Asifbad");
//        telanganaDistricts.add("District C");
        String[] dists =  {
        "Adilabad",
                "Bhadradri Kothagudem",
                "Hyderabad",
                "Jagtial",
                "Jangaon",
                "Jayashankar Bhupalpally",
                "Jogulamba Gadwal",
                "Kamareddy",
                "Karimnagar",
                "Khammam",
                "Komaram Bheem Asifabad",
                "Mahabubabad",
                "Mahabubnagar",
                "Mancherial",
                "Medak",
                "Medchal-Malkajgiri",
                "Mulugu",
                "Nagarkurnool",
                "Nalgonda",
                "Narayanpet",
                "Nirmal",
                "Nizamabad",
                "Peddapalli",
                "Rajanna Sircilla",
                "Ranga Reddy",
                "Sangareddy",
                "Siddipet",
                "Suryapet",
                "Vikarabad",
                "Wanaparthy",
                "Warangal Rural",
                "Warangal Urban",
                "Yadadri Bhuvanagiri" };

        List<String> telanganaDistricts = Arrays.asList(dists);

        // Add more districts as needed
        return telanganaDistricts;
    }
}

