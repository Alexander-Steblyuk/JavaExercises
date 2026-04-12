package ru.steblyuk.hw.formatters;

import org.springframework.format.Formatter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import ru.steblyuk.hw.dto.AuthorDto;

import java.util.List;
import java.util.Locale;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AuthorDtoFormatter implements Formatter<AuthorDto> {

    private static final Pattern AUTHOR_DTO_FIELDS_PATTERN = Pattern.compile("((?<=id=)\\d+(?=;\\s))|((?<=fullName=).+(?=]))");

    private static final int ID_RESULT_INDEX = 0;
    private static final int FULL_NAME_RESULT_INDEX = 1;

    @Override
    @NonNull
    public AuthorDto parse(@NonNull String text, @NonNull Locale locale) {
        Matcher matcher = AUTHOR_DTO_FIELDS_PATTERN.matcher(text);
        List<String> results = matcher.results()
                .map(MatchResult::group)
                .toList();
        Long id = Long.parseLong(results.get(ID_RESULT_INDEX));
        String fullName = results.get(FULL_NAME_RESULT_INDEX);
        return new AuthorDto(id, fullName);
    }

    @Override
    @NonNull
    public String print(@NonNull AuthorDto author, @NonNull Locale locale) {
        return author.toString();
    }
}
