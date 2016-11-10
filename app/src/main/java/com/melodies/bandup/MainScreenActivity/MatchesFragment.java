package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.ChatActivity;
import com.melodies.bandup.MainScreenActivity.adapters.MyMatchesRecyclerViewAdapter;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.helper_classes.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MatchesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private AdView mAdView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MatchesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MatchesFragment newInstance(int columnCount) {
        MatchesFragment fragment = new MatchesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    MyMatchesRecyclerViewAdapter mmrva;
    List<User> matchItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matchItems = new ArrayList<>();
        mmrva = new MyMatchesRecyclerViewAdapter(getActivity(), matchItems, mListener);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }


        String url = getActivity().getResources().getString(R.string.api_address).concat("/matches");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject item = response.getJSONObject(i);
                                User user = new User();
                                if (!item.isNull("_id"))      user.id = item.getString("_id");
                                if (!item.isNull("username")) user.name = item.getString("username");
                                if (!item.isNull("image")) {
                                    JSONObject imgObj = item.getJSONObject("image");
                                    if (!imgObj.isNull("url")) user.imgURL = imgObj.getString("url");

                                }
                                mmrva.addUser(user);


                            } catch (JSONException e) {
                                Toast.makeText(getActivity(), "Could not parse the JSON object.", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleySingleton.getInstance(getActivity()).checkCauseOfError(error);

                    }
                }
        );
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches_list, container, false);

        // Adding ad Banner
        mAdView = (AdView)view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(mmrva);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(User item);
    }

    public void onClickChat(User user) {
        Intent myIntent = new Intent(getActivity(), ChatActivity.class);
        myIntent.putExtra("SEND_TO_USER_ID", user.id);
        myIntent.putExtra("SEND_TO_USERNAME", user.name);
        startActivity(myIntent);
    }
}
