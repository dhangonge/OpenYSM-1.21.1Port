package com.elfmcys.yesstevemodel.geckolib3.core.molang;

import com.elfmcys.yesstevemodel.YesSteveModel;
import com.elfmcys.yesstevemodel.client.renderer.AnimationDebugOverlay;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.binding.PrimaryBinding;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.value.IValue;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.value.FloatValue;
import com.elfmcys.yesstevemodel.geckolib3.core.molang.value.MolangValue;
import com.elfmcys.yesstevemodel.molang.MolangEngine;
import com.elfmcys.yesstevemodel.molang.parser.ParseException;
import com.elfmcys.yesstevemodel.util.log.ChatLogger;
import net.minecraft.network.chat.Component;

import java.util.Map;

public class MolangParser {

    private final MolangEngine engine;

    private final PrimaryBinding primaryBinding;

    public MolangParser(Map<String, Object> map) {
        this.primaryBinding = new PrimaryBinding(map);
        this.engine = MolangEngine.fromCustomBinding(this.primaryBinding);
    }

    @SuppressWarnings("unused")
    public IValue parseExpression(String molangExpression, boolean isScript) {
        try {
            return parseExpressionUnsafe(molangExpression, isScript);
        } catch (Exception e) {
            if (AnimationDebugOverlay.isDebugActive()) {
                YesSteveModel.LOGGER.error("Failed to parse molang expression: {}\n{}", e.getMessage(), molangExpression);
                ChatLogger.INSTANCE.logComponent(Component.translatable("error.yes_steve_model.parse_molang_exp").append(e.getMessage()).append("\n----------------------\n").append(molangExpression.replace("\r\n", "\n").replace("\r", "\n")).append("\n----------------------"));
            } else {
                YesSteveModel.LOGGER.debug("Failed to parse molang expression: {}\n{}", e.getMessage(), molangExpression);
            }
            return FloatValue.ZERO;
        }
    }

    public IValue parseExpressionUnsafe(String molangExpression, boolean isScript) throws ParseException {
        MolangValue value = new MolangValue(this.engine.parse(isScript ? stripComments(molangExpression) : molangExpression), isScript);
        this.primaryBinding.dispose();
        return value;
    }

    private static String stripComments(String input) {
        StringBuilder resultBuilder = new StringBuilder(input.length());
        boolean inBlockComment = false;
        boolean inLineComment = false;
        boolean inStringLiteral = false;
        int parenDepth = 0;
        boolean seenEqualsOnLine = false;

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (inStringLiteral) {
                if (currentChar == '\'') {
                    inStringLiteral = false;
                }
                resultBuilder.append(currentChar);
                continue;
            }

            if (inLineComment) {
                if (currentChar == '\r' || currentChar == '\n') {
                    inLineComment = false;
                    seenEqualsOnLine = false;
                    resultBuilder.append('\n');
                }
                continue;
            }

            if (inBlockComment) {
                if (currentChar == '*' && i + 1 < input.length()) {
                    char nextChar = input.charAt(i + 1);
                    if (nextChar == '/') {
                        inBlockComment = false;
                        i++;
                    }
                }
                continue;
            }

            if (currentChar == '\r' || currentChar == '\n') {
                seenEqualsOnLine = false;
                resultBuilder.append('\n');
                continue;
            }

            if (currentChar == '(') {
                parenDepth++;
            } else if (currentChar == ')') {
                if (parenDepth > 0) parenDepth--;
            }

            if (currentChar == '=') {
                seenEqualsOnLine = true;
            }

            if (currentChar == '\'') {
                if (parenDepth > 0 || seenEqualsOnLine) {
                    // normal string: inside function call or after assignment/comparison
                    inStringLiteral = true;
                    resultBuilder.append('\'');
                } else {
                    // annotation string: find matching last quote on the same line
                    int lineEnd = i;
                    while (lineEnd < input.length() && input.charAt(lineEnd) != '\r' && input.charAt(lineEnd) != '\n') {
                        lineEnd++;
                    }

                    int quoteCount = 1;
                    for (int j = i + 1; j < lineEnd; j++) {
                        if (input.charAt(j) == '\'') quoteCount++;
                    }

                    if (quoteCount % 2 != 0) {
                        // odd quotes: malformed, treat as normal string
                        inStringLiteral = true;
                        resultBuilder.append('\'');
                    } else {
                        int lastQuote = lineEnd - 1;
                        while (lastQuote > i && input.charAt(lastQuote) != '\'') {
                            lastQuote--;
                        }
                        i = lastQuote;
                    }
                }
                continue;
            }

            if (currentChar == '/' && i + 1 < input.length()) {
                char nextChar = input.charAt(i + 1);

                if (nextChar == '/') {
                    inLineComment = true;
                    i++;
                    continue;
                }

                if (nextChar == '*') {
                    inBlockComment = true;
                    i++;
                    continue;
                }
            }

            resultBuilder.append(currentChar);
        }

        return resultBuilder.toString();
    }

    public IValue toFloatValue(double d) {
        return new FloatValue((float) d);
    }

    public void reset() {
        this.primaryBinding.reset();
    }
}