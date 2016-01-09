# chess-tools

## Lightweight library of tools for various chess tasks.

 The components span from a simple position data structure or FEN string parsing to UCI protocol implementation or even headless app for UCI tournaments.

  chess-tools is an attempt to consolidate a maven module that I have written in a rush for my various java chess shenanigans.
  It needs a lot of love expressed as dead-code purging, fixing nonsensical or broken elements, establishing a clear API, test writing and the like.


I captured an old scribbled plan and for the time being I put it in here:

* pgn game test -> should be able to test for mate, round (unknown), etc.
* CONTINUE: PlyCount?, TimeControl, Comments!!!!! -> rewrite the parsing to account for comments!!!
* CONTINUE: FEN tag -> parsing and formatting
* CONTINUE: things around timer
* implement 50 move rule
* insufficient mating material... how is this actually enforced / implemented in pgn / practiced in engines?
* FIXME: use Position.fullMoveClock instead of GameNode.ordinalNumber; possible also rename to Position.fullMoveCounter/Number