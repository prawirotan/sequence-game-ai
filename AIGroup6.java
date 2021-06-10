import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map;

//Author: Edbert Anthony dan Trian Hidayat

public class AIGroup6 extends AI {
    public Random rand = new Random();

    public AIGroup6(int id, int team, GameModel gameModel) {
        super(id, team, gameModel);
        uselessCellIdx = new ArrayList<>();
    }

    @Override
    public int discardCard() {
        // always discard dead cards
        List<Card> deadCards = getDeadCards(getGameModel().getSequenceBoard());
        if (deadCards == null)
            return 0;

        int ttlDiscardedCards = deadCards.size();
        for (Card eachCard : deadCards) {
            getGameModel().setSelectedCard(eachCard);
            getGameModel().discardSelectedCard();
        }
        return ttlDiscardedCards;
    }

    @Override
    public Card evaluateHand() {
        int nilaiTemporary;
        Card selectedCard = null;
        List<Card> handCards = this.getCards();
        setTwinCardEnabled(false);

        //always play twin cards
        List<Card> twinCards = getTwinCards();
        if(twinCards != null){
            if(getGameModel().getSequenceBoard().getAvailableCellsIdx(twinCards.get(0)).size() == 2 ) {
                selectedCard = twinCards.get(0);
                setTwinCardEnabled(true);
                return selectedCard;
            }
        }

        ArrayList<Integer> nilaiKartu = new ArrayList<>(); 
        ArrayList<Integer> Letak = new ArrayList<>();
        ArrayList<Card> Kartu = new ArrayList<>();


        //untuk turn pertama
        if(getGameModel().usedDeck.isEmpty() == true){
            selectedCard = handCards.get(rand.nextInt(handCards.size()));
            getGameModel().setSelectedCard(selectedCard);
            List<Integer> each = getGameModel().getSequenceBoard().getAvailableCellsIdx(selectedCard);
            setSelectedIdxOneDim(each.get(rand.nextInt(each.size())));
            return selectedCard;
        }

        else{ //turn-turn berikutnya
            for(Card i : handCards){
                List<Integer> each = getGameModel().getSequenceBoard().getAvailableCellsIdx(i);
                for(Integer j : each){
                    int x = j%SequenceBoard.BOARD_WIDTH;
                    int y = j%SequenceBoard.BOARD_HEIGHT;
                    Kartu.add(i);
                    Letak.add(j);
                    nilaiKartu.add(findHeuristic(x, y));
                }
            }
        }

        nilaiTemporary = nilaiKartu.indexOf(Collections.max(nilaiKartu));
        selectedCard = Kartu.get(nilaiTemporary);
        setSelectedIdxOneDim(Letak.get(nilaiTemporary));
        return selectedCard;
    }

    public int findHeuristic(int x, int y) {
        //prepare the variable
        int NilaiHeuristik = 0;
        int temp;
        boolean isSequence = false;
        Cell[][] cells = getGameModel().getSequenceBoard().getCells();

        //check column (LINE_EAST)
        for(int incCol=-4; incCol<=0; incCol++){
            isSequence = false;
            temp = 0;
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
                if(cells[idxCol + n][y].isWildCell()){
                    temp += 1;
                }

                //if there is a sequence AND this is not the first encounter (more than 1 cell are already a sequence)
                if(cells[idxCol + n][y].isSequence()){
                    if(isSequence)
                        break;
                    isSequence = true;
                }

                //sum the mark (value) of each cell)
                if(cells[idxCol + n][y].getMark() == this.getTeam()){
                    temp += 1;
                }
            }
            NilaiHeuristik += temp;

            //after this code

            if(NilaiHeuristik<temp){
                NilaiHeuristik=temp;
             }
        }

        //check row (LINE_SOUTH)
        for(int incRow=-4; incRow<=0; incRow++){
            isSequence = false;
            temp = 0;
            int idxRow = y+incRow;

            //if out of bound (leftmost column), then next iteration
            if(idxRow < 0)
                continue;
            //if out of bound (rightmost column), then break (do not need to check next column)
            if(idxRow + 4 >= SequenceBoard.BOARD_HEIGHT)
                break;

            //check the next 5 cells to the south
            for(int n=0; n<=4; n++){
                //if there is a wild cell
                if(cells[x][idxRow+n].isWildCell()){
                    temp += 1;
                }

                //if there is a sequence AND this is not the first encounter (more than 1 cell are already a sequence)
                if(cells[x][idxRow+n].isSequence()){
                    if(isSequence)
                        break;
                    isSequence = true;
                }

                //sum the mark (value) of each cell)
                if(cells[x][idxRow+n].getMark() == this.getTeam()){
                    temp += 1;
                }
            }
                NilaiHeuristik += temp;

                //after this code

                if(NilaiHeuristik<temp){
                    NilaiHeuristik=temp;
                }
        }

        //check row (LINE_SOUTH_WEST)
        for(int inc=4; inc>=0; inc--){
            isSequence = false;
            temp = 0;
            int idxCol = x+inc;
            int idxRow = y-inc;

            //if out of bound (leftmost column), then next iteration
            if(idxRow < 0 || idxCol >= SequenceBoard.BOARD_WIDTH)
                continue;
            //if out of bound (rightmost column), then break (do not need to check next column)
            if(idxRow + 4 >= SequenceBoard.BOARD_HEIGHT || idxCol - 4 < 0)
                break;

            //check the next 5 cells to the south west
            for(int n=0; n<=4; n++){
                //if there is a wild cell
                if(cells[idxCol - n][idxRow + n].isWildCell()){
                    temp += 1;
                }

                //if there is a sequence AND this is not the first encounter (more than 1 cell are already a sequence)
                if(cells[idxCol - n][idxRow + n].isSequence()){
                    if(isSequence)
                        break;
                    isSequence = true;
                }

                //sum the mark (value) of each cell)
                if(cells[idxCol - n][idxRow + n].getMark() == this.getTeam()){
                    temp += 1;
                }
            }
            NilaiHeuristik += temp;

            //after this code

            if(NilaiHeuristik<temp){
                NilaiHeuristik=temp;
            }
        }

        //check row (LINE_SOUTH_EAST)
        for(int inc=-4; inc<=0; inc++){
            isSequence = false;
            temp = 0;
            int idxCol = x+inc;
            int idxRow = y+inc;

            //if out of bound (leftmost column), then next iteration
            if(idxRow < 0 || idxCol < 0 )
                continue;
            //if out of bound (rightmost column), then break (do not need to check next column)
            if(idxRow + 4 >= SequenceBoard.BOARD_HEIGHT || idxCol + 4 < SequenceBoard.BOARD_WIDTH)
                break;

            //check the next 5 cells to the south east
            for(int n=0; n<=4; n++){
                //if there is a wild cell
                if(cells[idxCol+n][idxRow+n].isWildCell()){
                    temp += 1;
                }

                //if there is a sequence AND this is not the first encounter (more than 1 cell are already a sequence)
                if(cells[idxCol+n][idxRow+n].isSequence()){
                    if(isSequence)
                        break;
                    isSequence = true;
                }

                //sum the mark (value) of each cell)
                if(cells[idxCol+n][idxRow+n].getMark() == this.getTeam()){
                    temp += 1;
                }
            }
            NilaiHeuristik += temp;

            //after this code

            if(NilaiHeuristik<temp){
                NilaiHeuristik=temp;
            }
        }
        return NilaiHeuristik;
    }
}
