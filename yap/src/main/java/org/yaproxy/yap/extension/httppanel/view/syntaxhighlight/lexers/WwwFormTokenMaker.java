// DO NOT EDIT
// Generated by JFlex 1.8.2 http://jflex.de/
// source: org/yaproxy/yap/extension/httppanel/view/syntaxhighlight/lexers/WwwFormTokenMaker.flex
/*
 * This file is based on the flex files from RSyntaxTextArea.
 *
 * WwwFormTokenMaker.java - Generates tokens for HTTP request body syntax highlighting.
 * Specifically to "application/x-www-form-urlencoded" body.
 */
package org.yaproxy.yap.extension.httppanel.view.syntaxhighlight.lexers;

import javax.swing.text.Segment;
import org.fife.ui.rsyntaxtextarea.AbstractJFlexTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;

/**
 * A parser of HTTP request body {@code application/x-www-form-urlencoded}.
 *
 * @see <a href="http://www.w3.org/TR/html401/interact/forms.html#form-content-type">Form content
 *     type</a>
 */
// See https://github.com/jflex-de/jflex/issues/222
@SuppressWarnings("FallThrough")
public class WwwFormTokenMaker extends AbstractJFlexTokenMaker {

    /** This character denotes the end of file. */
    public static final int YYEOF = -1;

    // Lexical states.
    public static final int YYINITIAL = 0;

    public static final int VALUE = 2;

    /**
     * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l ZZ_LEXSTATE[l+1] is the state
     * in the DFA for the lexical state l at the beginning of a line l is of the form l = 2*k, k a
     * non negative integer
     */
    private static final int[] ZZ_LEXSTATE = {0, 0, 1, 1};

    /** Top-level table for translating characters to character classes */
    private static final int[] ZZ_CMAP_TOP = zzUnpackcmap_top();

    private static final String ZZ_CMAP_TOP_PACKED_0 = "\1\0\u10ff\u0100";

    private static int[] zzUnpackcmap_top() {
        int[] result = new int[4352];
        int offset = 0;
        offset = zzUnpackcmap_top(ZZ_CMAP_TOP_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackcmap_top(String packed, int offset, int[] result) {
        int i = 0;
        /* index in packed string  */
        int j = offset;
        /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do result[j++] = value;
            while (--count > 0);
        }
        return j;
    }

    /** Second-level tables for translating characters to character classes */
    private static final int[] ZZ_CMAP_BLOCKS = zzUnpackcmap_blocks();

    private static final String ZZ_CMAP_BLOCKS_PACKED_0 =
            "\45\0\1\1\1\2\3\0\1\3\1\4\1\0\2\3"
                    + "\1\0\12\5\3\0\1\6\3\0\6\5\24\3\4\0"
                    + "\1\3\1\0\6\5\24\3\u0185\0";

    private static int[] zzUnpackcmap_blocks() {
        int[] result = new int[512];
        int offset = 0;
        offset = zzUnpackcmap_blocks(ZZ_CMAP_BLOCKS_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackcmap_blocks(String packed, int offset, int[] result) {
        int i = 0;
        /* index in packed string  */
        int j = offset;
        /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do result[j++] = value;
            while (--count > 0);
        }
        return j;
    }

    /** Translates DFA states to action switch labels. */
    private static final int[] ZZ_ACTION = zzUnpackAction();

    private static final String ZZ_ACTION_PACKED_0 = "\2\0\2\1\1\2\1\3\1\1\1\4\1\5\1\6" + "\4\0";

    private static int[] zzUnpackAction() {
        int[] result = new int[14];
        int offset = 0;
        offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackAction(String packed, int offset, int[] result) {
        int i = 0;
        /* index in packed string  */
        int j = offset;
        /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do result[j++] = value;
            while (--count > 0);
        }
        return j;
    }

    /** Translates a state to a row index in the transition table */
    private static final int[] ZZ_ROWMAP = zzUnpackRowMap();

    private static final String ZZ_ROWMAP_PACKED_0 =
            "\0\0\0\7\0\16\0\25\0\34\0\16\0\43\0\16" + "\0\52\0\61\0\70\0\25\0\77\0\43";

    private static int[] zzUnpackRowMap() {
        int[] result = new int[14];
        int offset = 0;
        offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackRowMap(String packed, int offset, int[] result) {
        int i = 0;
        /* index in packed string  */
        int j = offset;
        /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int high = packed.charAt(i++) << 16;
            result[j++] = high | packed.charAt(i++);
        }
        return j;
    }

    /** The transition table of the DFA */
    private static final int[] ZZ_TRANS = zzUnpackTrans();

    private static final String ZZ_TRANS_PACKED_0 =
            "\1\3\1\4\1\3\1\5\1\3\1\5\1\6\1\3"
                    + "\1\7\1\10\1\11\1\12\1\11\1\3\14\0\1\13"
                    + "\2\0\1\14\1\0\3\5\6\0\1\15\2\0\1\16"
                    + "\1\0\1\11\1\0\1\11\5\0\1\12\7\0\1\5"
                    + "\6\0\1\11\1\0";

    private static int[] zzUnpackTrans() {
        int[] result = new int[70];
        int offset = 0;
        offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackTrans(String packed, int offset, int[] result) {
        int i = 0;
        /* index in packed string  */
        int j = offset;
        /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            value--;
            do result[j++] = value;
            while (--count > 0);
        }
        return j;
    }

    /** Error code for "Unknown internal scanner error". */
    private static final int ZZ_UNKNOWN_ERROR = 0;

    /** Error code for "could not match input". */
    private static final int ZZ_NO_MATCH = 1;

    /** Error code for "pushback value was too large". */
    private static final int ZZ_PUSHBACK_2BIG = 2;

    /**
     * Error messages for {@link #ZZ_UNKNOWN_ERROR}, {@link #ZZ_NO_MATCH}, and {@link
     * #ZZ_PUSHBACK_2BIG} respectively.
     */
    private static final String[] ZZ_ERROR_MSG = {
        "Unknown internal scanner error",
        "Error: could not match input",
        "Error: pushback value was too large"
    };

    /** ZZ_ATTRIBUTE[aState] contains the attributes of state {@code aState} */
    private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();

    private static final String ZZ_ATTRIBUTE_PACKED_0 = "\2\0\1\11\2\1\1\11\1\1\1\11\2\1\4\0";

    private static int[] zzUnpackAttribute() {
        int[] result = new int[14];
        int offset = 0;
        offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
        return result;
    }

    private static int zzUnpackAttribute(String packed, int offset, int[] result) {
        int i = 0;
        /* index in packed string  */
        int j = offset;
        /* index in unpacked array */
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do result[j++] = value;
            while (--count > 0);
        }
        return j;
    }

    /** Input device. */
    private java.io.Reader zzReader;

    /** Current state of the DFA. */
    private int zzState;

    /** Current lexical state. */
    private int zzLexicalState = YYINITIAL;

    /**
     * This buffer contains the current text to be matched and is the source of the {@link
     * #yytext()} string.
     */
    private char[] zzBuffer;

    /** Text position at the last accepting state. */
    private int zzMarkedPos;

    /** Current text position in the buffer. */
    private int zzCurrentPos;

    /** Marks the beginning of the {@link #yytext()} string in the buffer. */
    private int zzStartRead;

    /** Marks the last character in the buffer, that has been read from input. */
    private int zzEndRead;

    /**
     * Whether the scanner is at the end of file.
     *
     * @see #yyatEOF
     */
    private boolean zzAtEOF;

    /**
     * The number of occupied positions in {@link #zzBuffer} beyond {@link #zzEndRead}.
     *
     * <p>When a lead/high surrogate has been read from the input stream into the final {@link
     * #zzBuffer} position, this will have a value of 1; otherwise, it will have a value of 0.
     */
    private int zzFinalHighSurrogate = 0;

    /** Number of newlines encountered up to the start of the matched text. */
    @SuppressWarnings("unused")
    private int yyline;

    /** Number of characters from the last newline up to the start of the matched text. */
    @SuppressWarnings("unused")
    private int yycolumn;

    /** Number of characters up to the start of the matched text. */
    @SuppressWarnings("unused")
    private long yychar;

    /** Whether the scanner is currently at the beginning of a line. */
    @SuppressWarnings("unused")
    private boolean zzAtBOL = true;

    /** Whether the user-EOF-code has already been executed. */
    @SuppressWarnings("unused")
    private boolean zzEOFDone;

    /* user code: */
    /**
     * Adds the token specified to the current linked list of tokens as an "end token;" that is, at
     * <code>zzMarkedPos</code>.
     *
     * @param tokenType The token's type.
     */
    private void addEndToken(int tokenType) {
        addToken(zzMarkedPos, zzMarkedPos, tokenType);
    }

    /**
     * Adds the token specified to the current linked list of tokens.
     *
     * @param tokenType The token's type.
     */
    private void addToken(int tokenType) {
        addToken(zzStartRead, zzMarkedPos - 1, tokenType);
    }

    /**
     * Adds the token specified to the current linked list of tokens.
     *
     * @param tokenType The token's type.
     */
    private void addToken(int start, int end, int tokenType) {
        int so = start + offsetShift;
        addToken(zzBuffer, start, end, tokenType, so);
    }

    /**
     * Adds the token specified to the current linked list of tokens.
     *
     * @param array The character array.
     * @param start The starting offset in the array.
     * @param end The ending offset in the array.
     * @param tokenType The token's type.
     * @param startOffset The offset in the document at which this token occurs.
     */
    @Override
    public void addToken(char[] array, int start, int end, int tokenType, int startOffset) {
        super.addToken(array, start, end, tokenType, startOffset);
        zzStartRead = zzMarkedPos;
    }

    /**
     * Returns the first token in the linked list of tokens generated from <code>text</code>. This
     * method must be implemented by subclasses so they can correctly implement syntax highlighting.
     *
     * @param text The text from which to get tokens.
     * @param initialTokenType The token type we should start with.
     * @param startOffset The offset into the document at which <code>text</code> starts.
     * @return The first <code>Token</code> in a linked list representing the syntax highlighted
     *     text.
     */
    @Override
    public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
        resetTokenList();
        this.offsetShift = -text.offset + startOffset;
        // Start off in the proper state.
        s = text;
        yyreset(zzReader);
        yybegin(YYINITIAL);
        return yylex();
    }

    /**
     * Refills the input buffer.
     *
     * @return <code>true</code> if EOF was reached, otherwise <code>false</code>.
     */
    private boolean zzRefill() {
        return zzCurrentPos >= s.offset + s.count;
    }

    /**
     * Resets the scanner to read from a new input stream. Does not close the old reader.
     *
     * <p>All internal variables are reset, the old input stream <b>cannot</b> be reused (internal
     * buffer is discarded and lost). Lexical state is set to {@code YY_INITIAL}.
     *
     * @param reader the new input stream
     */
    public final void yyreset(java.io.Reader reader) {
        // 's' has been updated.
        zzBuffer = s.array;
        /*
         * We replaced the line below with the two below it because zzRefill
         * no longer "refills" the buffer (since the way we do it, it's always
         * "full" the first time through, since it points to the segment's
         * array).  So, we assign zzEndRead here.
         */
        // zzStartRead = zzEndRead = s.offset;
        zzStartRead = s.offset;
        zzEndRead = zzStartRead + s.count;
        zzCurrentPos = zzMarkedPos = s.offset;
        zzLexicalState = YYINITIAL;
        zzReader = reader;
        zzAtBOL = true;
        zzAtEOF = false;
        zzEOFDone = false;
        zzFinalHighSurrogate = zzCurrentPos;
    }

    int errorPos = -1;

    private void handleInvalidToken() {
        if (errorPos != -1) {
            int currentZzStartRead = zzStartRead;
            addToken(errorPos, zzStartRead - 1, Token.IDENTIFIER);
            zzStartRead = currentZzStartRead;
            errorPos = -1;
        }
    }

    private void startInvalidToken() {
        if (errorPos == -1) {
            errorPos = zzMarkedPos - 1;
        }
    }

    /**
     * Creates a new scanner
     *
     * @param in the java.io.Reader to read input from.
     */
    public WwwFormTokenMaker(java.io.Reader in) {
        this.zzReader = in;
    }

    /** Translates raw input code points to DFA table row */
    private static int zzCMap(int input) {
        int offset = input & 255;
        return offset == input
                ? ZZ_CMAP_BLOCKS[offset]
                : ZZ_CMAP_BLOCKS[ZZ_CMAP_TOP[input >> 8] | offset];
    }

    /**
     * Closes the input reader.
     *
     * @throws java.io.IOException if the reader could not be closed.
     */
    @Override
    public final void yyclose() throws java.io.IOException {
        // indicate end of file
        zzAtEOF = true;
        // invalidate buffer
        zzEndRead = zzStartRead;
        if (zzReader != null) {
            zzReader.close();
        }
    }

    /**
     * Returns whether the scanner has reached the end of the reader it reads from.
     *
     * @return whether the scanner has reached EOF.
     */
    public final boolean yyatEOF() {
        return zzAtEOF;
    }

    /**
     * Returns the current lexical state.
     *
     * @return the current lexical state.
     */
    public final int yystate() {
        return zzLexicalState;
    }

    /**
     * Enters a new lexical state.
     *
     * @param newState the new lexical state
     */
    @Override
    public final void yybegin(int newState) {
        zzLexicalState = newState;
    }

    /**
     * Returns the text matched by the current regular expression.
     *
     * @return the matched text.
     */
    @Override
    public final String yytext() {
        return new String(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
    }

    /**
     * Returns the character at the given position from the matched text.
     *
     * <p>It is equivalent to {@code yytext().charAt(pos)}, but faster.
     *
     * @param position the position of the character to fetch. A value from 0 to {@code
     *     yylength()-1}.
     * @return the character at {@code position}.
     */
    public final char yycharat(int position) {
        return zzBuffer[zzStartRead + position];
    }

    /**
     * How many characters were matched.
     *
     * @return the length of the matched text region.
     */
    public final int yylength() {
        return zzMarkedPos - zzStartRead;
    }

    /**
     * Reports an error that occurred while scanning.
     *
     * <p>In a well-formed scanner (no or only correct usage of {@code yypushback(int)} and a
     * match-all fallback rule) this method will only be called with things that "Can't Possibly
     * Happen".
     *
     * <p>If this method is called, something is seriously wrong (e.g. a JFlex bug producing a
     * faulty scanner etc.).
     *
     * <p>Usual syntax/scanner level error handling should be done in error fallback rules.
     *
     * @param errorCode the code of the error message to display.
     */
    private static void zzScanError(int errorCode) {
        String message;
        try {
            message = ZZ_ERROR_MSG[errorCode];
        } catch (ArrayIndexOutOfBoundsException e) {
            message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
        }
        throw new Error(message);
    }

    /**
     * Pushes the specified amount of characters back into the input stream.
     *
     * <p>They will be read again by then next call of the scanning method.
     *
     * @param number the number of characters to be read again. This number must not be greater than
     *     {@link #yylength()}.
     */
    public void yypushback(int number) {
        if (number > yylength()) zzScanError(ZZ_PUSHBACK_2BIG);
        zzMarkedPos -= number;
    }

    /**
     * Resumes scanning until the next regular expression is matched, the end of input is
     * encountered or an I/O-Error occurs.
     *
     * @return the next token.
     */
    @SuppressWarnings("fallthrough")
    public Token yylex() {
        int zzInput;
        int zzAction;
        // cached fields:
        int zzCurrentPosL;
        int zzMarkedPosL;
        int zzEndReadL = zzEndRead;
        char[] zzBufferL = zzBuffer;
        int[] zzTransL = ZZ_TRANS;
        int[] zzRowMapL = ZZ_ROWMAP;
        int[] zzAttrL = ZZ_ATTRIBUTE;
        while (true) {
            zzMarkedPosL = zzMarkedPos;
            zzAction = -1;
            zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
            zzState = ZZ_LEXSTATE[zzLexicalState];
            // set up zzAction for empty match case:
            int zzAttributes = zzAttrL[zzState];
            if ((zzAttributes & 1) == 1) {
                zzAction = zzState;
            }
            zzForAction:
            {
                while (true) {
                    if (zzCurrentPosL < zzEndReadL) {
                        zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
                        zzCurrentPosL += Character.charCount(zzInput);
                    } else if (zzAtEOF) {
                        zzInput = YYEOF;
                        break zzForAction;
                    } else {
                        // store back cached positions
                        zzCurrentPos = zzCurrentPosL;
                        zzMarkedPos = zzMarkedPosL;
                        boolean eof = zzRefill();
                        // get translated positions and possibly new buffer
                        zzCurrentPosL = zzCurrentPos;
                        zzMarkedPosL = zzMarkedPos;
                        zzBufferL = zzBuffer;
                        zzEndReadL = zzEndRead;
                        if (eof) {
                            zzInput = YYEOF;
                            break zzForAction;
                        } else {
                            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
                            zzCurrentPosL += Character.charCount(zzInput);
                        }
                    }
                    int zzNext = zzTransL[zzRowMapL[zzState] + zzCMap(zzInput)];
                    if (zzNext == -1) break zzForAction;
                    zzState = zzNext;
                    zzAttributes = zzAttrL[zzState];
                    if ((zzAttributes & 1) == 1) {
                        zzAction = zzState;
                        zzMarkedPosL = zzCurrentPosL;
                        if ((zzAttributes & 8) == 8) break zzForAction;
                    }
                }
            }
            // store back cached position
            zzMarkedPos = zzMarkedPosL;
            if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
                zzAtEOF = true;
                switch (zzLexicalState) {
                    case YYINITIAL:
                        {
                            handleInvalidToken();
                            addNullToken();
                            return firstToken;
                        }
                        // fall though
                    case 15:
                        break;
                    case VALUE:
                        {
                            handleInvalidToken();
                            addNullToken();
                            return firstToken;
                        }
                        // fall though
                    case 16:
                        break;
                    default:
                        return null;
                }
            } else {
                switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
                    case 1:
                        {
                            startInvalidToken();
                        }
                        // fall through
                    case 7:
                        break;
                    case 2:
                        {
                            handleInvalidToken();
                            addToken(Token.RESERVED_WORD);
                        }
                        // fall through
                    case 8:
                        break;
                    case 3:
                        {
                            handleInvalidToken();
                            addToken(Token.SEPARATOR);
                            yybegin(VALUE);
                        }
                        // fall through
                    case 9:
                        break;
                    case 4:
                        {
                            handleInvalidToken();
                            addToken(Token.VARIABLE);
                            yybegin(YYINITIAL);
                        }
                        // fall through
                    case 10:
                        break;
                    case 5:
                        {
                            handleInvalidToken();
                            addToken(Token.DATA_TYPE);
                        }
                        // fall through
                    case 11:
                        break;
                    case 6:
                        {
                            handleInvalidToken();
                            addToken(Token.COMMENT_DOCUMENTATION);
                        }
                        // fall through
                    case 12:
                        break;
                    default:
                        zzScanError(ZZ_NO_MATCH);
                }
            }
        }
    }

    public WwwFormTokenMaker() {}
}
