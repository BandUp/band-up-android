package com.melodies.bandup;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.melodies.bandup.main_screen_activity.ProfileFragment.DEFAULT;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RECEIVER_ID = "receiverId";
    private static final String ARG_RECEIVER_USERNAME = "receiverUserName";

    // TODO: Rename and change types of parameters
    private String mReceiverId;
    private String mReceiverUsername;

    private OnFragmentInteractionListener mListener;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param receiverId
     * @param receiverUsername
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String receiverId, String receiverUsername) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECEIVER_ID, receiverId);
        args.putString(ARG_RECEIVER_USERNAME, receiverUsername);
        fragment.setArguments(args);
        return fragment;
    }
    // Adding ad Banner
    private void getAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mReceiverId = getArguments().getString(ARG_RECEIVER_ID);
            mReceiverUsername = getArguments().getString(ARG_RECEIVER_USERNAME);
        }
        mAdapter = new ChatRecyclerAdapter(getActivity(), null, getUserId());

    }

    EditText txtMessage;
    RecyclerView mRecycler;
    private ChatRecyclerAdapter mAdapter;
    Button btnSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        mAdView = (AdView) rootView.findViewById(R.id.adView);

        txtMessage = (EditText) rootView.findViewById(R.id.txtMessage);

        btnSend = (Button) rootView.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = txtMessage.getText().toString();

                // Do not allow user to send empty string.
                if (message.equals("")) {
                    return;
                }

                // Create the JSON object to send the message.
                final JSONObject msgObject = new JSONObject();
                try {
                    msgObject.put("nick", mReceiverId);
                    msgObject.put("message", message);
                } catch (JSONException e) {
                    FirebaseCrash.report(e);
                }

                if (((ChatActivity) getActivity()).sendMessage(msgObject)) {
                    txtMessage.setText("");

                    displayMessage(getUserId(), message, true);

                    mRecycler.scrollToPosition(0);
                }
            }
        });

        mRecycler = (RecyclerView) rootView.findViewById(R.id.recyclerList);
        mRecycler.setAdapter(mAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecycler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecycler.scrollToPosition(0);
                        }
                    }, 100);
                }
            }
        });
        getChatHistory();
        // Inflate the layout for this fragment
        getAd();
        return rootView;
    }

    public void getChatHistory() {
        if (getActivity() == null) {
            return;
        }

        String url = getResources().getString(R.string.api_address).concat("/chat_history/").concat(mReceiverId);

        // Get chat history.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mAdapter.clearChatHistory();
                        if (!response.isNull("chatHistory")) {
                            try {
                                JSONArray chatHistory = response.getJSONArray("chatHistory");
                                for (int i = 0; i < chatHistory.length(); i++) {
                                    JSONObject item = chatHistory.getJSONObject(i);
                                    if (!item.isNull("message")) {
                                        displayMessage(item.getString("sender"), item.getString("message"), false);
                                    }
                                }
                                mAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                FirebaseCrash.report(e);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error == null) {
                            // TODO: Display Error Message
                            return;
                        }

                        if (error.networkResponse == null) {
                            // TODO: Display Error Message
                            return;
                        }

                        // If there is no chat found. No worries.
                        if (error.networkResponse.statusCode != 404) {
                            VolleySingleton.getInstance(getActivity()).checkCauseOfError(error);
                        }
                    }
                }
        );

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /* Adds the message to the ScrollView. */
    public void displayMessage(String sender, String message, Boolean notify) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.text = message;
        chatMessage.senderUserId = sender;

        mAdapter.addMessage(chatMessage, notify);
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
    public String getUserId() {
        SharedPreferences srdPref = getActivity().getSharedPreferences("UserIdRegister", Context.MODE_PRIVATE);
        String userId = srdPref.getString("userID", DEFAULT);
        return (!userId.equals(DEFAULT)) ? userId : "No data Found";
    }


    public String getReceiverId() {
        return mReceiverId;
    }

    public RecyclerView getRecyclerView() {
        return mRecycler;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
