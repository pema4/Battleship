package battleship.models;

import java.io.Serializable;

/**
 * Сообщение о начале игры
 * Отправляется одним игроком другому после того, как игроки получили имена друг друга
 */
public class GameStart implements Serializable {
    private static final long serialVersionUID = 1230813119888438577L;
}
