package game;

public enum GamePos {
    playCall, //waiting for a play to be called
    rsp, //the rsp after a playcall
    shortRun, //currently doing a short run
    longRun, //currently doing a long run
    longRunFumble, //the offence just fumbled a long run
    shortPass, //currently doing a short pass
    longPass, //currently doing a long pass
    defenceChoice, //the defence is making a choice to sack or intercept
    defenceRoll, //the defence is rolling to see what happens (sack/interception)
    touchdown, //just scored a touch down, waiting for decision
    extraPoint, //currently kicking an extra point
    twoPointConversion, //currently doing a two point conversion
    kickoff, //choosing which kind of kickoff
    regularKick, //about to do a regular kickoff
    onsideKick, //about to do an onside kick
    touchback, //waiting for the returning team to take knee or run it
    kickReturn, //the returning team is rolling for a kick return
    interception, //waiting for a roll for where the ball was intercepted
}
