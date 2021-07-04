package com.example.trackme.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.trackme.R;
import com.example.trackme.adapters.MyAdapter;
import com.example.trackme.MyClickListner;

import com.example.trackme.model.ContactUser;
import com.example.trackme.model.Select;

import com.example.trackme.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class Contacts extends Fragment {
    ArrayList<ContactUser> fbList;
    HashMap<String, String> map;
    Cursor cursor;

    private View rootView;
    private RecyclerView list;
    private ArrayList<ContactUser> dataset;
    private MyAdapter adapter;
    private FirebaseAuth AuthObj=null;
    private String User_id=null;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthObj=FirebaseAuth.getInstance();
        User_id= AuthObj.getUid();
        dataset = new ArrayList<>();
        map = new HashMap<>();
        fbList = new ArrayList<>();


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS

            }, 2020);
        } else {
            System.out.println("heloooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
            getContacts();

        }


    }

    //loading contacts from firebase
    private void loadFbList() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/user");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Iterable<DataSnapshot> children = snapshot.getChildren();
                GenericTypeIndicator<User> indicator = new GenericTypeIndicator<User>() {

                };
                for (DataSnapshot it : children) {
                    User value = it.getValue(indicator);
                    if(value.getId().equals(AuthObj.getUid())){
                        continue;
                    }




                    if (map.containsKey(value.getPhn()) || map.containsKey(value.getPhn().substring(3))) {

                        ContactUser contact = new ContactUser(map.get(value.getPhn()), value.getPhn(), value.getId());
                        fbList.add(contact);
                    }


                }

                adapter = new MyAdapter(getActivity(), fbList);
                setAdapterListner();

                try {
                    list.setAdapter(adapter);
                } catch (Exception e) {
                    System.out.println(e.getMessage() + "---------------------------------------------------");
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.frag_contact, container, false);

        list = rootView.findViewById(R.id.list);
        list.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL));
        list.addItemDecoration(new DividerItemDecoration(list.getContext(), DividerItemDecoration.VERTICAL));


        return rootView;
    }

    private void setAdapterListner() {

        adapter.setMyItemClickListner(new MyClickListner() {
            @Override
            public void MyClick(int pos) {
             SendSignal(pos);
            }
        });
    }






    //send messege
    private void SendSignal(int pos) {
        String phone=fbList.get(pos).getPhNo();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("/SendList/"+AuthObj.getUid());

        ref.child("Phone").setValue(phone);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2020) {
            if (permissions[0].equals(Manifest.permission.READ_CONTACTS)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "hogya", Toast.LENGTH_SHORT).show();
                getContacts();
            }
        }

    }

    public void getContacts() {

        // create cursor and query the data
        cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        getActivity().startManagingCursor(cursor);

        // data is a array of String type which is
        // used to store Number ,Names and id.
        String[] data = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone._ID};


        while (cursor.moveToNext()) {

            String phone = cursor.getString(cursor.getColumnIndex(data[0])).replaceAll("\\s", "");
            String name = cursor.getString(cursor.getColumnIndex(data[1]));
            map.put(phone, name);
            ContactUser contactUser = new ContactUser(name, phone, "id");
            dataset.add(contactUser);


        }



        // creation of adapter using SimpleCursorAdapter class
        loadFbList();

    }

}

