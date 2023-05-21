package dsl.antlr4;
// Generated from grammar/RelAlgebra.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RelAlgebraLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, SELECTION=8, PROJECTION=9, 
		JOIN=10, LEFTJOIN=11, RIGHTJOIN=12, UNION=13, CARTESIANPRODUCT=14, IMPORT=15, 
		AS=16, RELATION=17, PREDICATE=18, DIGIT=19, PATH_TOKEN=20, WS=21;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "SELECTION", 
			"PROJECTION", "JOIN", "LEFTJOIN", "RIGHTJOIN", "UNION", "CARTESIANPRODUCT", 
			"IMPORT", "AS", "RELATION", "PREDICATE", "DIGIT", "PATH_TOKEN", "WS", 
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", 
			"O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'.head'", "'<'", "','", "'>'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, "SELECTION", "PROJECTION", 
			"JOIN", "LEFTJOIN", "RIGHTJOIN", "UNION", "CARTESIANPRODUCT", "IMPORT", 
			"AS", "RELATION", "PREDICATE", "DIGIT", "PATH_TOKEN", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public RelAlgebraLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "RelAlgebra.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\27\u0117\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22\7\22\u00c4\n\22\f\22"+
		"\16\22\u00c7\13\22\3\23\3\23\7\23\u00cb\n\23\f\23\16\23\u00ce\13\23\3"+
		"\23\3\23\3\24\3\24\3\25\3\25\7\25\u00d6\n\25\f\25\16\25\u00d9\13\25\3"+
		"\25\3\25\3\26\6\26\u00de\n\26\r\26\16\26\u00df\3\26\3\26\3\27\3\27\3\30"+
		"\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37"+
		"\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)"+
		"\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60\3\u00cc\2\61\3\3\5\4\7"+
		"\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22"+
		"#\23%\24\'\25)\26+\27-\2/\2\61\2\63\2\65\2\67\29\2;\2=\2?\2A\2C\2E\2G"+
		"\2I\2K\2M\2O\2Q\2S\2U\2W\2Y\2[\2]\2_\2\3\2!\4\2C\\c|\6\2\62;C\\aac|\3"+
		"\2\62;\6\2\61;C\\aac|\5\2\13\f\17\17\"\"\4\2CCcc\4\2DDdd\4\2EEee\4\2F"+
		"Fff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4\2NNnn\4"+
		"\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2VVvv\4\2WWw"+
		"w\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\2\u0100\2\3\3\2\2\2\2\5\3"+
		"\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2"+
		"\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3"+
		"\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'"+
		"\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\3a\3\2\2\2\5c\3\2\2\2\7i\3\2\2\2\tk\3\2"+
		"\2\2\13m\3\2\2\2\ro\3\2\2\2\17q\3\2\2\2\21s\3\2\2\2\23}\3\2\2\2\25\u0088"+
		"\3\2\2\2\27\u008d\3\2\2\2\31\u0096\3\2\2\2\33\u00a0\3\2\2\2\35\u00a6\3"+
		"\2\2\2\37\u00b7\3\2\2\2!\u00be\3\2\2\2#\u00c1\3\2\2\2%\u00c8\3\2\2\2\'"+
		"\u00d1\3\2\2\2)\u00d3\3\2\2\2+\u00dd\3\2\2\2-\u00e3\3\2\2\2/\u00e5\3\2"+
		"\2\2\61\u00e7\3\2\2\2\63\u00e9\3\2\2\2\65\u00eb\3\2\2\2\67\u00ed\3\2\2"+
		"\29\u00ef\3\2\2\2;\u00f1\3\2\2\2=\u00f3\3\2\2\2?\u00f5\3\2\2\2A\u00f7"+
		"\3\2\2\2C\u00f9\3\2\2\2E\u00fb\3\2\2\2G\u00fd\3\2\2\2I\u00ff\3\2\2\2K"+
		"\u0101\3\2\2\2M\u0103\3\2\2\2O\u0105\3\2\2\2Q\u0107\3\2\2\2S\u0109\3\2"+
		"\2\2U\u010b\3\2\2\2W\u010d\3\2\2\2Y\u010f\3\2\2\2[\u0111\3\2\2\2]\u0113"+
		"\3\2\2\2_\u0115\3\2\2\2ab\7=\2\2b\4\3\2\2\2cd\7\60\2\2de\7j\2\2ef\7g\2"+
		"\2fg\7c\2\2gh\7f\2\2h\6\3\2\2\2ij\7>\2\2j\b\3\2\2\2kl\7.\2\2l\n\3\2\2"+
		"\2mn\7@\2\2n\f\3\2\2\2op\7*\2\2p\16\3\2\2\2qr\7+\2\2r\20\3\2\2\2st\5Q"+
		")\2tu\5\65\33\2uv\5C\"\2vw\5\65\33\2wx\5\61\31\2xy\5S*\2yz\5=\37\2z{\5"+
		"I%\2{|\5G$\2|\22\3\2\2\2}~\5K&\2~\177\5O(\2\177\u0080\5I%\2\u0080\u0081"+
		"\5? \2\u0081\u0082\5\65\33\2\u0082\u0083\5\61\31\2\u0083\u0084\5S*\2\u0084"+
		"\u0085\5=\37\2\u0085\u0086\5I%\2\u0086\u0087\5G$\2\u0087\24\3\2\2\2\u0088"+
		"\u0089\5? \2\u0089\u008a\5I%\2\u008a\u008b\5=\37\2\u008b\u008c\5G$\2\u008c"+
		"\26\3\2\2\2\u008d\u008e\5C\"\2\u008e\u008f\5\65\33\2\u008f\u0090\5\67"+
		"\34\2\u0090\u0091\5S*\2\u0091\u0092\5? \2\u0092\u0093\5I%\2\u0093\u0094"+
		"\5=\37\2\u0094\u0095\5G$\2\u0095\30\3\2\2\2\u0096\u0097\5O(\2\u0097\u0098"+
		"\5=\37\2\u0098\u0099\59\35\2\u0099\u009a\5;\36\2\u009a\u009b\5S*\2\u009b"+
		"\u009c\5? \2\u009c\u009d\5I%\2\u009d\u009e\5=\37\2\u009e\u009f\5G$\2\u009f"+
		"\32\3\2\2\2\u00a0\u00a1\5U+\2\u00a1\u00a2\5G$\2\u00a2\u00a3\5=\37\2\u00a3"+
		"\u00a4\5I%\2\u00a4\u00a5\5G$\2\u00a5\34\3\2\2\2\u00a6\u00a7\5\61\31\2"+
		"\u00a7\u00a8\5-\27\2\u00a8\u00a9\5O(\2\u00a9\u00aa\5S*\2\u00aa\u00ab\5"+
		"\65\33\2\u00ab\u00ac\5Q)\2\u00ac\u00ad\5=\37\2\u00ad\u00ae\5-\27\2\u00ae"+
		"\u00af\5G$\2\u00af\u00b0\5K&\2\u00b0\u00b1\5O(\2\u00b1\u00b2\5I%\2\u00b2"+
		"\u00b3\5\63\32\2\u00b3\u00b4\5U+\2\u00b4\u00b5\5\61\31\2\u00b5\u00b6\5"+
		"S*\2\u00b6\36\3\2\2\2\u00b7\u00b8\5=\37\2\u00b8\u00b9\5E#\2\u00b9\u00ba"+
		"\5K&\2\u00ba\u00bb\5I%\2\u00bb\u00bc\5O(\2\u00bc\u00bd\5S*\2\u00bd \3"+
		"\2\2\2\u00be\u00bf\5-\27\2\u00bf\u00c0\5Q)\2\u00c0\"\3\2\2\2\u00c1\u00c5"+
		"\t\2\2\2\u00c2\u00c4\t\3\2\2\u00c3\u00c2\3\2\2\2\u00c4\u00c7\3\2\2\2\u00c5"+
		"\u00c3\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6$\3\2\2\2\u00c7\u00c5\3\2\2\2"+
		"\u00c8\u00cc\7]\2\2\u00c9\u00cb\13\2\2\2\u00ca\u00c9\3\2\2\2\u00cb\u00ce"+
		"\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cc\u00ca\3\2\2\2\u00cd\u00cf\3\2\2\2\u00ce"+
		"\u00cc\3\2\2\2\u00cf\u00d0\7_\2\2\u00d0&\3\2\2\2\u00d1\u00d2\t\4\2\2\u00d2"+
		"(\3\2\2\2\u00d3\u00d7\7\61\2\2\u00d4\u00d6\t\5\2\2\u00d5\u00d4\3\2\2\2"+
		"\u00d6\u00d9\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00da"+
		"\3\2\2\2\u00d9\u00d7\3\2\2\2\u00da\u00db\t\3\2\2\u00db*\3\2\2\2\u00dc"+
		"\u00de\t\6\2\2\u00dd\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df\u00dd\3\2"+
		"\2\2\u00df\u00e0\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e2\b\26\2\2\u00e2"+
		",\3\2\2\2\u00e3\u00e4\t\7\2\2\u00e4.\3\2\2\2\u00e5\u00e6\t\b\2\2\u00e6"+
		"\60\3\2\2\2\u00e7\u00e8\t\t\2\2\u00e8\62\3\2\2\2\u00e9\u00ea\t\n\2\2\u00ea"+
		"\64\3\2\2\2\u00eb\u00ec\t\13\2\2\u00ec\66\3\2\2\2\u00ed\u00ee\t\f\2\2"+
		"\u00ee8\3\2\2\2\u00ef\u00f0\t\r\2\2\u00f0:\3\2\2\2\u00f1\u00f2\t\16\2"+
		"\2\u00f2<\3\2\2\2\u00f3\u00f4\t\17\2\2\u00f4>\3\2\2\2\u00f5\u00f6\t\20"+
		"\2\2\u00f6@\3\2\2\2\u00f7\u00f8\t\21\2\2\u00f8B\3\2\2\2\u00f9\u00fa\t"+
		"\22\2\2\u00faD\3\2\2\2\u00fb\u00fc\t\23\2\2\u00fcF\3\2\2\2\u00fd\u00fe"+
		"\t\24\2\2\u00feH\3\2\2\2\u00ff\u0100\t\25\2\2\u0100J\3\2\2\2\u0101\u0102"+
		"\t\26\2\2\u0102L\3\2\2\2\u0103\u0104\t\27\2\2\u0104N\3\2\2\2\u0105\u0106"+
		"\t\30\2\2\u0106P\3\2\2\2\u0107\u0108\t\31\2\2\u0108R\3\2\2\2\u0109\u010a"+
		"\t\32\2\2\u010aT\3\2\2\2\u010b\u010c\t\33\2\2\u010cV\3\2\2\2\u010d\u010e"+
		"\t\34\2\2\u010eX\3\2\2\2\u010f\u0110\t\35\2\2\u0110Z\3\2\2\2\u0111\u0112"+
		"\t\36\2\2\u0112\\\3\2\2\2\u0113\u0114\t\37\2\2\u0114^\3\2\2\2\u0115\u0116"+
		"\t \2\2\u0116`\3\2\2\2\7\2\u00c5\u00cc\u00d7\u00df\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}