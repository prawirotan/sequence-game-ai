import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AIGroup0 extends AI {
    public AIGroup0(int id, int team, GameModel gameModel){
        super(id, team, gameModel);
        uselessCellIdx = new ArrayList<>();
    }

    @Override
    public int discardCard() {
        //always discard dead cards
        List<Card> deadCards = getDeadCards(getGameModel().getSequenceBoard());
        if(deadCards == null)
            return 0;

        int ttlDiscardedCards = deadCards.size();
        for(Card eachCard : deadCards){
            getGameModel().setSelectedCard(eachCard);
            getGameModel().discardSelectedCard();
        }
        return ttlDiscardedCards;
    }

    @Override
    public Card evaluateHand() {
        Card selectedCard = null;
        setTwinCardEnabled(false);
        int bestIdxCellOneDim = 0;

        //always play twin cards
        List<Card> twinCards = getTwinCards();
        if(twinCards != null){
            if(getGameModel().getSequenceBoard().getAvailableCellsIdx(twinCards.get(0)).size() == 2 ) {
                selectedCard = twinCards.get(0);
                setTwinCardEnabled(true);
                return selectedCard;
            }
        }

        //always select the first card
        selectedCard = getCards().get(0);
        List<Integer> idx = getGameModel().getSequenceBoard().getAvailableCellsIdx(selectedCard);
        if(idx.size() > 0)
            setSelectedIdxOneDim(idx.get(0));
        return selectedCard;
    }

    public int[] findHeuristic(int x, int y) {
        //prepare the variable
        int[] maxMarkDir = new int[4];
        int h, temp;
        boolean isSequence = false;
        Cell[][] cells = getGameModel().getSequenceBoard().getCells();

        //check column (LINE_EAST)
        for(int incCol=-4; incCol<=0; incCol++){
            isSequence = false;
            h = temp = 0;
            int idxCol = x+incCol;

            //if out of bound (leftmost column), then next iteration
            if(idxCol < 0)
                continue;
            //if out of bound (rightmost column), then break (do not need to check next column)
            if(idxCol + 4 >= SequenceBoard.BOARD_WIDTH)
                break;

            //check the next 5 cells to the east
            for(int n=0; n<=4; n++){
                //if there is a wild cell
                if(cells[idxCol + n][y].isWildCell())
                    temp = this.getTeam();

                //if there is a sequence AND this is not the first encounter (more than 1 cell are already a sequence)
                if(cells[idxCol + n][y].isSequence()){
                    if(isSequence)
                        break;
                    isSequence = true;
                }

                //sum the mark (value) of each cell
                if(cells[idxCol + n][y].getMark() == this.getTeam())
                    h += cells[idxCol + n][y].getMark();
                if(idxCol + n == x)
                    h += this.getTeam();
            }
            h += temp;

            //after this code

            //check the total sum (h)
            if(h == 5*this.getTeam()){
                //create a sequence
            }
        }
        return maxMarkDir;
    }
}
