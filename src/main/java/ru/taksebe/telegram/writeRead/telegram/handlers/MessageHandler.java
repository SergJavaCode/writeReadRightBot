package ru.taksebe.telegram.writeRead.telegram.handlers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.taksebe.telegram.writeRead.constants.bot.BotMessageEnum;
import ru.taksebe.telegram.writeRead.constants.bot.ButtonNameEnum;
import ru.taksebe.telegram.writeRead.constants.bot.CallbackDataPartsEnum;
import ru.taksebe.telegram.writeRead.exceptions.DictionaryTooBigException;
import ru.taksebe.telegram.writeRead.exceptions.TelegramFileNotFoundException;
import ru.taksebe.telegram.writeRead.telegram.TelegramApiClient;
import ru.taksebe.telegram.writeRead.telegram.keyboards.InlineKeyboardMaker;
import ru.taksebe.telegram.writeRead.telegram.keyboards.ReplyKeyboardMaker;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageHandler {
    TelegramApiClient telegramApiClient;
    ReplyKeyboardMaker replyKeyboardMaker;
    InlineKeyboardMaker inlineKeyboardMaker;

    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString(); //получаем ID чата, что бы отправлять ответ в тот чат, который сделал запрос, а не в другой

        if (message.hasDocument()) {
            return addUserDictionary(chatId, message.getDocument().getFileId());
        }

        String inputText = message.getText();

        if (inputText == null) {
            throw new IllegalArgumentException();
        } else if (inputText.equals("/start")) { //обрабатываем кнопки постоянной клавиатуры
            return getStartMessage(chatId);
        } else if (inputText.equals(ButtonNameEnum.GET_TASKS_BUTTON.getButtonName())) {
            return getTasksMessage(chatId);
        } else if (inputText.equals(ButtonNameEnum.GET_DICTIONARY_BUTTON.getButtonName())) {
            return getDictionaryMessage(chatId);
        } else if (inputText.equals(ButtonNameEnum.UPLOAD_DICTIONARY_BUTTON.getButtonName())) {
            return new SendMessage(chatId, BotMessageEnum.UPLOAD_DICTIONARY_MESSAGE.getMessage());
        } else if (inputText.equals(ButtonNameEnum.HELP_BUTTON.getButtonName())) {
            SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getMessage());
            sendMessage.enableMarkdown(true);
            return sendMessage;
        } else {
            return new SendMessage(chatId, BotMessageEnum.NON_COMMAND_MESSAGE.getMessage()); //обработать текстовые сообщения, отличные от названий кнопок
        }
    }

    private SendMessage getStartMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.HELP_MESSAGE.getMessage()); //выводим сообщение при запуске /start (в данном случае из HELP)
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());//инициируем вывод постоянной клвиатуры
        return sendMessage;
    }

    private SendMessage getTasksMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.CHOOSE_DICTIONARY_MESSAGE.getMessage());
        sendMessage.setReplyMarkup(inlineKeyboardMaker.getInlineMessageButtons(
                CallbackDataPartsEnum.TASK_.name(), false//заглушка

        ));
        return sendMessage;
    }

    private SendMessage getDictionaryMessage(String chatId) {
        SendMessage sendMessage = new SendMessage(chatId, BotMessageEnum.CHOOSE_DICTIONARY_MESSAGE.getMessage());
        sendMessage.setReplyMarkup(new ReplyKeyboardMarkup()); //заглушка
        return sendMessage;
    }

    private SendMessage addUserDictionary(String chatId, String fileId) {
        try {
            return new SendMessage(chatId, BotMessageEnum.SUCCESS_UPLOAD_MESSAGE.getMessage());
        } catch (TelegramFileNotFoundException e) {
            return new SendMessage(chatId, BotMessageEnum.EXCEPTION_TELEGRAM_API_MESSAGE.getMessage());
        } catch (DictionaryTooBigException e) {
            return new SendMessage(chatId, BotMessageEnum.EXCEPTION_TOO_LARGE_DICTIONARY_MESSAGE.getMessage());
        } catch (Exception e) {
            return new SendMessage(chatId, BotMessageEnum.EXCEPTION_BAD_FILE_MESSAGE.getMessage());
        }
    }
}