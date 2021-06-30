package com.example.mangareader;

import java.util.Random;

public class SplashScreen {
    static String[] statuses = {"Getting things ready, please be patient"
            , "Beep beep loading beep beep"
            , "Hello there, please wait while I sort my stuff out"
            , "THIS IS AN EMERGENCY I AM ABOUT TO EXPLODE"
            , "Lol have fun waiting"
            , "Please drink some water while I get things ready"
            , "Wanna play a game while I load things?"
            , "LUCIO HEAL ME!!"
            , "Have fun reading"
            , "How's your day been?"
            , "(╯°□°)╯︵ ┻━┻"
            , "Make sure to take a break every now and then"
            , "Why does this take so long?"
            , "No it's not you, it's me *cries*"
            , "I'm trying to load this as soon as possible. JUST GIVE ME A MOMENT"
            , "I am once again asking for your financial support"
            , "AAAAAAAAAAAAAAAAHHHHHHHHHHHHHh"
            , "Blame your internet speed for being so slow, not me"
            , "SCREAM"
            , "I'm generating a lot of buttons as we speak."
            , "How'd we end up here?"
            , "(っ◔◡◔)っ ♥ not you lol ♥"
    };

    public static String returnQuote() {

        Random random = new Random();
        String sts = statuses[random.nextInt(statuses.length)];
        return sts;
    }
}
