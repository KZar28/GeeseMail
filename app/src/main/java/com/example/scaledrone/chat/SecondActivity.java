package com.example.scaledrone.chat;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.ObservableRoomListener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.util.ArrayList;
import java.util.Random;

public class SecondActivity extends AppCompatActivity implements RoomListener {

    // replace this with a real channelID from Scaledrone dashboard
    private String channelID = "oE3rZMb7qJbb3Cj3";
    private String roomName = "observable-room";
    private EditText editText;
    private EditText membersList;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private int numMembers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        membersList = (EditText) findViewById(R.id.editText2);
        numMembers = 0;

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        MemberData data = new MemberData(getRandomName(), getRandomColor());

        scaledrone = new Scaledrone(channelID, data);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("[Log] Scaledrone connection open");
                scaledrone.subscribe(roomName,SecondActivity.this);

                /* This is the Member List section */
                Room room = scaledrone.subscribe("observable-room", new RoomListener() {

                    // Overwrite regular room listener methods
                    public void onOpen(Room room) {
                        System.out.println("[Log] Connected to room - Observable");
                    }

                    @Override
                    public void onOpenFailure(Room room, Exception ex) {
                        System.err.println(ex);
                    }
                    @Override
                    public void onMessage(Room room, final JsonNode json, final Member member) {
                        final ObjectMapper mapper = new ObjectMapper();
                        try {
                            final MemberData data = mapper.treeToValue(member.getClientData(), MemberData.class);
                            boolean belongsToCurrentUser = member.getId().equals(scaledrone.getClientID());
                            final Message message = new Message(json.asText(), data, belongsToCurrentUser);

                            System.out.println(" [Message Log] Sent Message by " + member.getClientData() + " Message = " + message);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageAdapter.add(message);
                                    messagesView.setSelection(messagesView.getCount() - 1);
                                }
                            });
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                    }
                });
                room.listenToObservableEvents( new ObservableRoomListener() {
                    @Override
                    public void onMembers(Room room, ArrayList<Member> members) {
                        // Emits an array of members that have joined the room. This event is only triggered once, right after the user has successfully connected to the observable room.
                        // Keep in mind that the session user will also be part of this array, so the minimum size of the array is 1

                        numMembers = members.size();
                        for (int i = 0; i < numMembers; i++){
                            Member member = members.get(i);
                            System.out.println(" [Log] Members Online = " + member.getClientData());
                        }

                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            //final MemberData memberList = mapper.treeToValue(member.getClientData(), MemberData.class);
                            //System.out.println(memberList.getName());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    membersList.setText(numMembers + " are online!");
                                }
                            });
                        } catch (Exception e) {
                            System.out.println(e);
                        }

                    }

                    @Override
                    public void onMemberJoin(Room room, Member member) {
                        // A new member joined the room.

                        /* Member Count Handler */
                        numMembers += 2;


                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            final MemberData memberList = mapper.treeToValue(member.getClientData(), MemberData.class);

                            String message = memberList.getName() + ", Welcome to BoolChat!";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    membersList.setText(memberList.getName() + " has joined!    " + numMembers + " Online!");
                                }
                            });

                            //System.out.println(message);
                            //scaledrone.publish(roomName, message);

                        } catch (JsonProcessingException e) {
                            System.out.println("[Error] Caught Member Join Exception -----> " + e);
                        }
                    }

                    @Override
                    public void onMemberLeave(Room room, Member member) {
                        // A member left the room (or disconnected)

                        /* Member Count Handler */
                        numMembers -= 2;


                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            final MemberData memberList = mapper.treeToValue(member.getClientData(), MemberData.class);

                            String message = "Goodbye, " + memberList.getName();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    membersList.setText(memberList.getName() + " has left!    " + numMembers + " Online!");
                                }
                            });

                            //System.out.println(message);
                            //scaledrone.publish(roomName, message);

                        } catch (JsonProcessingException e) {
                            System.out.println("[Error] Caught Member Leave Exception -----> " + e);
                        }

                    }
                });
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });
    }

    public void sendMessage(View view) {
        String message = editText.getText().toString();
        if (message.length() > 0) {
            scaledrone.publish(roomName, message);
            editText.getText().clear();
        }
    }

    @Override
    public void onOpen(Room room) {
        System.out.println(" [Log] Connected to room");
    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex);
    }

    @Override
    public void onMessage(Room room, final JsonNode json, final Member member) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final MemberData data = mapper.treeToValue(member.getClientData(), MemberData.class);
            boolean belongsToCurrentUser = member.getId().equals(scaledrone.getClientID());
            final Message message = new Message(json.asText(), data, belongsToCurrentUser);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String getRandomName() {
        // CLEAN VERSION ~~~~~
        //String[] adjs = {"autumn", "hidden", "bitter", "misty", "silent", "empty", "dry", "dark", "summer", "icy", "delicate", "quiet", "white", "cool", "spring", "winter", "patient", "twilight", "dawn", "crimson", "wispy", "weathered", "blue", "billowing", "broken", "cold", "damp", "falling", "frosty", "green", "long", "late", "lingering", "bold", "little", "morning", "muddy", "old", "red", "rough", "still", "small", "sparkling", "throbbing", "shy", "wandering", "withered", "wild", "black", "young", "holy", "solitary", "fragrant", "aged", "snowy", "proud", "floral", "restless", "divine", "polished", "ancient", "purple", "lively", "nameless"};
        //String[] nouns = {"waterfall", "river", "breeze", "moon", "rain", "wind", "sea", "morning", "snow", "lake", "sunset", "pine", "shadow", "leaf", "dawn", "glitter", "forest", "hill", "cloud", "meadow", "sun", "glade", "bird", "brook", "butterfly", "bush", "dew", "dust", "field", "fire", "flower", "firefly", "feather", "grass", "haze", "mountain", "night", "pond", "darkness", "snowflake", "silence", "sound", "sky", "shape", "surf", "thunder", "violet", "water", "wildflower", "wave", "water", "resonance", "sun", "wood", "dream", "cherry", "tree", "fog", "frost", "voice", "paper", "frog", "smoke", "star"};

        // DIRTY VERSION ~~~~~
        String[] adjs = {"Reezy", "Drunk", "Moist", "Bitter", "Nasty", "Saucey", "Raging", "Black", "Slutty", "Itchy", "Pompous", "Greasy", "Enormous", "Donald", "Angry", "Crimson", "Broken", "Little", "Throbbing", "Shy", "Wandering", "Withered", "Wild", "Cold", "Old", "Dusty", "Burning", "Musky", "Tittilating", "Cheesy"};
        String[] nouns = {"beaner", "river", "cracker", "cock", "boss", "bonobo", "daddy", "salami", "Charle", "phallus", "asshole", "ranchmaster", "dildo", "cunt", "Trump", "woman", "cooter", "bush", "Musk", "snowflake", "vapist", "analrapist", "gloryhole", "jababool", "seargent", "tentacle", "Chestacles", "nightcrawler"};

        return (
                adjs[(int) Math.floor(Math.random() * adjs.length)] +
                        " " +
                        nouns[(int) Math.floor(Math.random() * nouns.length)]
        );
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }


    /* Floating action button handler */
    public void ButtonHandler(View View){

        EditText membersBar = View.findViewById(R.id.editText2);
        membersBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View View) {
                startActivity(new Intent(View.getContext(), MembersScreen.class));
            }
        });
        /*FloatingActionButton floatingActionButton = View.findViewById(R.id.floatingActionButton2);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View View){
                startActivity(new Intent(View.getContext(), MembersScreen.class));
            }
        });*/
    }
}


class MemberData {
    private String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberData(){

    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
