// Generated from AQL.g4 by ANTLR 4.1
package edu.common.dynamicextensions.query.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class AQLLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, WS=2, SELECT=3, WHERE=4, MONTHS=5, YEARS=6, OR=7, AND=8, NOT=9, 
		LP=10, RP=11, MOP=12, OP=13, INT=14, FLOAT=15, YEAR=16, MONTH=17, DAY=18, 
		DIGIT=19, SLITERAL=20, ESC=21, ID=22, ARITH_OP=23, FIELD=24, QUOTE=25;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"','", "WS", "'select'", "'where'", "'months'", "'years'", "'or'", "'and'", 
		"'not'", "'('", "')'", "MOP", "OP", "INT", "FLOAT", "YEAR", "MONTH", "DAY", 
		"DIGIT", "SLITERAL", "ESC", "ID", "ARITH_OP", "FIELD", "'\"'"
	};
	public static final String[] ruleNames = {
		"T__0", "WS", "SELECT", "WHERE", "MONTHS", "YEARS", "OR", "AND", "NOT", 
		"LP", "RP", "MOP", "OP", "INT", "FLOAT", "YEAR", "MONTH", "DAY", "DIGIT", 
		"SLITERAL", "ESC", "ID", "ARITH_OP", "FIELD", "SGUTS", "QUOTE"
	};


	public AQLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "AQL.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 1: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\33\u00d4\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\3\2\3\2\3\3\6\3;\n\3\r\3\16\3<\3\3\3\3\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3"+
		"\n\3\13\3\13\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\5\rr\n\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u0080\n\16\3\17"+
		"\5\17\u0083\n\17\3\17\6\17\u0086\n\17\r\17\16\17\u0087\3\20\5\20\u008b"+
		"\n\20\3\20\6\20\u008e\n\20\r\20\16\20\u008f\3\20\3\20\6\20\u0094\n\20"+
		"\r\20\16\20\u0095\3\21\6\21\u0099\n\21\r\21\16\21\u009a\3\21\3\21\3\22"+
		"\6\22\u00a0\n\22\r\22\16\22\u00a1\3\22\3\22\3\23\6\23\u00a7\n\23\r\23"+
		"\16\23\u00a8\3\23\3\23\3\24\3\24\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3"+
		"\27\3\27\7\27\u00b8\n\27\f\27\16\27\u00bb\13\27\3\30\3\30\3\31\3\31\5"+
		"\31\u00c1\n\31\3\31\3\31\3\31\3\31\7\31\u00c7\n\31\f\31\16\31\u00ca\13"+
		"\31\3\32\3\32\7\32\u00ce\n\32\f\32\16\32\u00d1\13\32\3\33\3\33\2\34\3"+
		"\3\1\5\4\2\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r"+
		"\1\31\16\1\33\17\1\35\20\1\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27"+
		"\1-\30\1/\31\1\61\32\1\63\2\1\65\33\1\3\2\13\5\2\13\f\17\17\"\"\4\2>>"+
		"@@\4\2[[{{\4\2OOoo\4\2FFff\4\2$$^^\5\2C\\aac|\6\2\62;C\\aac|\5\2,-//\61"+
		"\61\u00e6\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2"+
		"\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2"+
		"\2/\3\2\2\2\2\61\3\2\2\2\2\65\3\2\2\2\3\67\3\2\2\2\5:\3\2\2\2\7@\3\2\2"+
		"\2\tG\3\2\2\2\13M\3\2\2\2\rT\3\2\2\2\17Z\3\2\2\2\21]\3\2\2\2\23a\3\2\2"+
		"\2\25e\3\2\2\2\27g\3\2\2\2\31q\3\2\2\2\33\177\3\2\2\2\35\u0082\3\2\2\2"+
		"\37\u008a\3\2\2\2!\u0098\3\2\2\2#\u009f\3\2\2\2%\u00a6\3\2\2\2\'\u00ac"+
		"\3\2\2\2)\u00ae\3\2\2\2+\u00b2\3\2\2\2-\u00b5\3\2\2\2/\u00bc\3\2\2\2\61"+
		"\u00c0\3\2\2\2\63\u00cf\3\2\2\2\65\u00d2\3\2\2\2\678\7.\2\28\4\3\2\2\2"+
		"9;\t\2\2\2:9\3\2\2\2;<\3\2\2\2<:\3\2\2\2<=\3\2\2\2=>\3\2\2\2>?\b\3\2\2"+
		"?\6\3\2\2\2@A\7u\2\2AB\7g\2\2BC\7n\2\2CD\7g\2\2DE\7e\2\2EF\7v\2\2F\b\3"+
		"\2\2\2GH\7y\2\2HI\7j\2\2IJ\7g\2\2JK\7t\2\2KL\7g\2\2L\n\3\2\2\2MN\7o\2"+
		"\2NO\7q\2\2OP\7p\2\2PQ\7v\2\2QR\7j\2\2RS\7u\2\2S\f\3\2\2\2TU\7{\2\2UV"+
		"\7g\2\2VW\7c\2\2WX\7t\2\2XY\7u\2\2Y\16\3\2\2\2Z[\7q\2\2[\\\7t\2\2\\\20"+
		"\3\2\2\2]^\7c\2\2^_\7p\2\2_`\7f\2\2`\22\3\2\2\2ab\7p\2\2bc\7q\2\2cd\7"+
		"v\2\2d\24\3\2\2\2ef\7*\2\2f\26\3\2\2\2gh\7+\2\2h\30\3\2\2\2ij\7k\2\2j"+
		"r\7p\2\2kl\7p\2\2lm\7q\2\2mn\7v\2\2no\7\"\2\2op\7k\2\2pr\7p\2\2qi\3\2"+
		"\2\2qk\3\2\2\2r\32\3\2\2\2s\u0080\t\3\2\2tu\7@\2\2u\u0080\7?\2\2vw\7>"+
		"\2\2w\u0080\7?\2\2x\u0080\7?\2\2yz\7#\2\2z\u0080\7?\2\2{|\7n\2\2|}\7k"+
		"\2\2}~\7m\2\2~\u0080\7g\2\2\177s\3\2\2\2\177t\3\2\2\2\177v\3\2\2\2\177"+
		"x\3\2\2\2\177y\3\2\2\2\177{\3\2\2\2\u0080\34\3\2\2\2\u0081\u0083\7/\2"+
		"\2\u0082\u0081\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\3\2\2\2\u0084\u0086"+
		"\5\'\24\2\u0085\u0084\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0085\3\2\2\2"+
		"\u0087\u0088\3\2\2\2\u0088\36\3\2\2\2\u0089\u008b\7/\2\2\u008a\u0089\3"+
		"\2\2\2\u008a\u008b\3\2\2\2\u008b\u008d\3\2\2\2\u008c\u008e\5\'\24\2\u008d"+
		"\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u008d\3\2\2\2\u008f\u0090\3\2"+
		"\2\2\u0090\u0091\3\2\2\2\u0091\u0093\7\60\2\2\u0092\u0094\5\'\24\2\u0093"+
		"\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0093\3\2\2\2\u0095\u0096\3\2"+
		"\2\2\u0096 \3\2\2\2\u0097\u0099\5\'\24\2\u0098\u0097\3\2\2\2\u0099\u009a"+
		"\3\2\2\2\u009a\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\u009c\3\2\2\2\u009c"+
		"\u009d\t\4\2\2\u009d\"\3\2\2\2\u009e\u00a0\5\'\24\2\u009f\u009e\3\2\2"+
		"\2\u00a0\u00a1\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a3"+
		"\3\2\2\2\u00a3\u00a4\t\5\2\2\u00a4$\3\2\2\2\u00a5\u00a7\5\'\24\2\u00a6"+
		"\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00a6\3\2\2\2\u00a8\u00a9\3\2"+
		"\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ab\t\6\2\2\u00ab&\3\2\2\2\u00ac\u00ad"+
		"\4\62;\2\u00ad(\3\2\2\2\u00ae\u00af\7$\2\2\u00af\u00b0\5\63\32\2\u00b0"+
		"\u00b1\7$\2\2\u00b1*\3\2\2\2\u00b2\u00b3\7^\2\2\u00b3\u00b4\t\7\2\2\u00b4"+
		",\3\2\2\2\u00b5\u00b9\t\b\2\2\u00b6\u00b8\t\t\2\2\u00b7\u00b6\3\2\2\2"+
		"\u00b8\u00bb\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba.\3"+
		"\2\2\2\u00bb\u00b9\3\2\2\2\u00bc\u00bd\t\n\2\2\u00bd\60\3\2\2\2\u00be"+
		"\u00c1\5\35\17\2\u00bf\u00c1\5-\27\2\u00c0\u00be\3\2\2\2\u00c0\u00bf\3"+
		"\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00c3\7\60\2\2\u00c3\u00c8\5-\27\2\u00c4"+
		"\u00c5\7\60\2\2\u00c5\u00c7\5-\27\2\u00c6\u00c4\3\2\2\2\u00c7\u00ca\3"+
		"\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\62\3\2\2\2\u00ca"+
		"\u00c8\3\2\2\2\u00cb\u00ce\5+\26\2\u00cc\u00ce\n\7\2\2\u00cd\u00cb\3\2"+
		"\2\2\u00cd\u00cc\3\2\2\2\u00ce\u00d1\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf"+
		"\u00d0\3\2\2\2\u00d0\64\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d2\u00d3\7$\2\2"+
		"\u00d3\66\3\2\2\2\23\2<q\177\u0082\u0087\u008a\u008f\u0095\u009a\u00a1"+
		"\u00a8\u00b9\u00c0\u00c8\u00cd\u00cf";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}