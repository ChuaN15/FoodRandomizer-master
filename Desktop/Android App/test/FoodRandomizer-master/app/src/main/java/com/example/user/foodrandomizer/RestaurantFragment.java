package com.example.user.foodrandomizer;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    RecyclerView recyclerView;
    SharedPreferences pref;
    Set<String> setList;
    public static RestaurantAdapter adapter;
    public static ArrayList<RestaurantModel> restaurantList;

    //private MyFragmentListener mListener;
    public RestaurantFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.restaurants_fragment, container, false);

        //Add restaurants.
        recyclerView = view.findViewById(R.id.recyclerView);


        pref = this.getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode

        setList = new HashSet<String>(pref.getStringSet("folder", new HashSet<String>()));
        List<String> setList2 = new ArrayList<>(setList);

        restaurantList = new ArrayList<>();

        for (int i = 0; i < setList2.size(); i++) {
            restaurantList.add(new RestaurantModel(setList2.get(i),"", ""));
        }


        adapter = new RestaurantAdapter(this.getActivity(), restaurantList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());

        Log.e("asd",linearLayoutManager.toString());
        RecyclerView.LayoutManager mLayoutManager = linearLayoutManager;
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        return view;
    }

/*
    public BigInteger getCursor(){
        return this.cursor;
    }
    public void setCursor(BigInteger cursorValue){
        this.cursor = cursorValue;
        Log.e("setCursor","set" + String.valueOf(this.cursor));
    }

    private void loadTwitterFriends() {
        Log.e("CursorIn",String.valueOf(getCursor()));
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().list(loggedUserTwitterId,getCursor(),200).enqueue(new retrofit2.Callback<FriendsResponseModel>() {
            @Override
            public void onResponse(Call<FriendsResponseModel> call, Response<FriendsResponseModel> response) {

                Log.e("onResponselol", "Is response success" + response.isSuccessful());
                twitterFriends = fetchResults(response);


                setCursor(fetchResults3(response));
                Log.e("next cursor", "Cursor:" + getCursor());
                for (int k=0;k<twitterFriends.size();k++){
                    Log.e("value",String.valueOf(twitterFriends.get(k).getFriends()));
                    ids.add(twitterFriends.get(k).getId());
                    object.add(twitterFriends.get(k));
                    friendsList.add(twitterFriends.get(k).getName());
                    imgid.add(twitterFriends.get(k).getProfilePictureUrl().replace("_normal",""));
                    user_id.add(twitterFriends.get(k).getScreenName());
                    Log.e("Twitter Friends","Id:"+twitterFriends.get(k).getId()+" Name:"+twitterFriends.get(k).getName()+" pickUrl:"+twitterFriends.get(k).getProfilePictureUrl());
                }

                Log.e("onResponse", "twitterfriends:" + twitterFriends.size());
                if (!getCursor().equals(BigInteger.ZERO)) {
                    loadTwitterFriends();
                }
                else{
                    getActivity().setTitle(String.valueOf(friendsList.size()) + " Following");
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<FriendsResponseModel> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });

    }



    private List<TwitterFriends> fetchResults(Response<FriendsResponseModel> response) {
        FriendsResponseModel responseModel = response.body();
        if (responseModel != null) {
            Log.e("lol","asd");
            return responseModel.getResults();
        }
        else{
            Log.e("1","dsa");
            //Server returning null objects
            return Collections.emptyList();
        }
    }


    private BigInteger fetchResults3(Response<FriendsResponseModel> response) {
        FriendsResponseModel responseModel = response.body();
        if (responseModel != null) {
            Log.e("lol",String.valueOf(responseModel.getNextCursor()));
            return responseModel.getNextCursor();
        }
        else{
            Log.e("2","dsa");
            return BigInteger.ZERO;
        }

    }


    public void sendMsg(long userId,String replyName,String msg){
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        Call<Tweet> call = myTwitterApiClient.getCustomTwitterService().sendPrivateMessage(userId,replyName,msg);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(getActivity(),"Message sent", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getActivity(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    */

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}