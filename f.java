import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordBot extends ListenerAdapter {

    private static final String BOT_TOKEN = "discord bot token goes here";
    private static final String SERVER_ID = "replace_with_server_id";

    public static void main(String[] args) throws Exception {
        JDABuilder.createDefault(BOT_TOKEN)
                .addEventListeners(new DiscordBot())
                .build();
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("player")) {
            String username = event.getOption("username").getAsString();
            getPlayerStats(event, username);
        }
    }

    private void getPlayerStats(SlashCommandEvent event, String username) {
        try {
            URL userIdUrl = new URL("https://api.roblox.com/users/get-by-username?username=" + username);
            HttpURLConnection userIdConnection = (HttpURLConnection) userIdUrl.openConnection();
            userIdConnection.setRequestMethod("GET");

            try (BufferedReader userIdReader = new BufferedReader(new InputStreamReader(userIdConnection.getInputStream()))) {
                StringBuilder userIdResponse = new StringBuilder();
                String line;
                while ((line = userIdReader.readLine()) != null) {
                    userIdResponse.append(line);
                }

                String userId = userIdResponse.toString().split("\"")[3];

                URL bloxflipUrl = new URL("https://api.bloxflip.com/user/lookup/" + userId);
                HttpURLConnection bloxflipConnection = (HttpURLConnection) bloxflipUrl.openConnection();
                bloxflipConnection.setRequestMethod("GET");

                try (BufferedReader bloxflipReader = new BufferedReader(new InputStreamReader(bloxflipConnection.getInputStream()))) {
                    StringBuilder bloxflipResponse = new StringBuilder();
                    String bloxflipLine;
                    while ((bloxflipLine = bloxflipReader.readLine()) != null) {
                        bloxflipResponse.append(bloxflipLine);
                    }

                    // Parse bloxflip info and send response
                    sendResponse(event, bloxflipResponse.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(SlashCommandEvent event, String responseJson) {
        // Parse the JSON response and create a MessageEmbed
        // ...

        // Send the MessageEmbed as a response
        event.getHook().sendMessageEmbeds(/* embed */).queue();
    }
}
