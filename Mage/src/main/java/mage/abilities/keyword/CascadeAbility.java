/*
 * Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of BetaSteward_at_googlemail.com.
 */
package mage.abilities.keyword;

import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.cards.SplitCard;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.game.ExileZone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.stack.Spell;
import mage.players.Player;

/**
 * @author BetaSteward_at_googlemail.com
 */
public class CascadeAbility extends TriggeredAbilityImpl {
    //20091005 - 702.82

    private final static String reminderText = " <i>(When you cast this spell, exile cards from the top of your library until you exile a nonland card that costs less."
            + " You may cast it without paying its mana cost. Put the exiled cards on the bottom in a random order.)</i>";
    private boolean withReminder;

    public CascadeAbility() {
        this(true);
    }

    public CascadeAbility(boolean withReminder) {
        super(Zone.STACK, new CascadeEffect());
        this.withReminder = withReminder;
    }

    public CascadeAbility(final CascadeAbility ability) {
        super(ability);
        this.withReminder = ability.withReminder;
    }

    @Override
    public boolean checkEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.SPELL_CAST;
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        Spell spell = game.getStack().getSpell(event.getTargetId());
        return spell != null && spell.getSourceId().equals(this.getSourceId());
    }

    @Override
    public String getRule() {
        StringBuilder sb = new StringBuilder("Cascade");
        if (withReminder) {
            sb.append(reminderText);
        }
        return sb.toString();
    }

    @Override
    public CascadeAbility copy() {
        return new CascadeAbility(this);
    }

}

class CascadeEffect extends OneShotEffect {

    public CascadeEffect() {
        super(Outcome.PutCardInPlay);
    }

    public CascadeEffect(CascadeEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card card;
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }
        ExileZone exile = game.getExile().createZone(source.getSourceId(), controller.getName() + " Cascade");
        int sourceCost = game.getCard(source.getSourceId()).getConvertedManaCost();
        do {
            card = controller.getLibrary().getFromTop(game);
            if (card == null) {
                break;
            }
            controller.moveCardsToExile(card, source, game, true, exile.getId(), exile.getName());
        } while (controller.isInGame() && (card.getCardType().contains(CardType.LAND) || !cardThatCostsLess(sourceCost, card, game)));

        controller.getLibrary().reset(); // set back empty draw state if that caused an empty draw

        if (card != null) {
            if (controller.chooseUse(outcome, "Use cascade effect on " + card.getLogName() + '?', source, game)) {
                controller.cast(card.getSpellAbility(), game, true);
            }
            // Move the remaining cards to the buttom of the library in a random order
            Cards cardsFromExile = new CardsImpl();
            Cards cardsToLibrary = new CardsImpl();
            cardsFromExile.addAll(exile);
            while (!cardsFromExile.isEmpty()) {
                card = cardsFromExile.getRandom(game);
                cardsFromExile.remove(card.getId());
                cardsToLibrary.add(card);
            }
            controller.putCardsOnBottomOfLibrary(cardsToLibrary, game, source, false);

        }
        return true;
    }

    @Override
    public CascadeEffect copy() {
        return new CascadeEffect(this);
    }

    private boolean cardThatCostsLess(int value, Card card, Game game) {
        if (card instanceof SplitCard) {
            return ((SplitCard) card).getLeftHalfCard().getConvertedManaCost() < value
                    || ((SplitCard) card).getRightHalfCard().getConvertedManaCost() < value;
        } else {
            return card.getConvertedManaCost() < value;
        }
    }
}
