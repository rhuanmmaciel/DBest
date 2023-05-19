package dsl.antlr4;

// Generated from RelAlgebra.g4 by ANTLR 4.7.2
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
		T__0=1, T__1=2, T__2=3, T__3=4, SELECTION=5, PROJECTION=6, JOIN=7, CARTESIAN=8, 
		ATTRIBUTE=9, PREDICATE=10, RELATION=11, WS=12;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "SELECTION", "PROJECTION", "JOIN", "CARTESIAN", 
			"ATTRIBUTE", "PREDICATE", "RELATION", "WS", "A", "B", "C", "D", "E", 
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", 
			"T", "U", "V", "W", "X", "Y", "Z"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'('", "')'", "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, "SELECTION", "PROJECTION", "JOIN", "CARTESIAN", 
			"ATTRIBUTE", "PREDICATE", "RELATION", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\16\u00cf\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\3\2\3\2\3\3\3\3\3\4\3"+
		"\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\n\3\n\7\n~\n\n\f\n\16\n\u0081\13\n\3\n\3\n\3\13\3\13"+
		"\7\13\u0087\n\13\f\13\16\13\u008a\13\13\3\13\3\13\3\f\3\f\7\f\u0090\n"+
		"\f\f\f\16\f\u0093\13\f\3\r\6\r\u0096\n\r\r\r\16\r\u0097\3\r\3\r\3\16\3"+
		"\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3"+
		"\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3"+
		"\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3"+
		"%\3&\3&\3\'\3\'\4\177\u0088\2(\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13"+
		"\25\f\27\r\31\16\33\2\35\2\37\2!\2#\2%\2\'\2)\2+\2-\2/\2\61\2\63\2\65"+
		"\2\67\29\2;\2=\2?\2A\2C\2E\2G\2I\2K\2M\2\3\2\37\4\2C\\c|\6\2\62;C\\aa"+
		"c|\5\2\13\f\17\17\"\"\4\2CCcc\4\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh"+
		"\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2Q"+
		"Qqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4"+
		"\2ZZzz\4\2[[{{\4\2\\\\||\2\u00b8\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2"+
		"\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2"+
		"\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\3O\3\2\2\2\5Q\3\2\2\2\7S\3"+
		"\2\2\2\tU\3\2\2\2\13W\3\2\2\2\ra\3\2\2\2\17l\3\2\2\2\21q\3\2\2\2\23{\3"+
		"\2\2\2\25\u0084\3\2\2\2\27\u008d\3\2\2\2\31\u0095\3\2\2\2\33\u009b\3\2"+
		"\2\2\35\u009d\3\2\2\2\37\u009f\3\2\2\2!\u00a1\3\2\2\2#\u00a3\3\2\2\2%"+
		"\u00a5\3\2\2\2\'\u00a7\3\2\2\2)\u00a9\3\2\2\2+\u00ab\3\2\2\2-\u00ad\3"+
		"\2\2\2/\u00af\3\2\2\2\61\u00b1\3\2\2\2\63\u00b3\3\2\2\2\65\u00b5\3\2\2"+
		"\2\67\u00b7\3\2\2\29\u00b9\3\2\2\2;\u00bb\3\2\2\2=\u00bd\3\2\2\2?\u00bf"+
		"\3\2\2\2A\u00c1\3\2\2\2C\u00c3\3\2\2\2E\u00c5\3\2\2\2G\u00c7\3\2\2\2I"+
		"\u00c9\3\2\2\2K\u00cb\3\2\2\2M\u00cd\3\2\2\2OP\7=\2\2P\4\3\2\2\2QR\7*"+
		"\2\2R\6\3\2\2\2ST\7+\2\2T\b\3\2\2\2UV\7.\2\2V\n\3\2\2\2WX\5? \2XY\5#\22"+
		"\2YZ\5\61\31\2Z[\5#\22\2[\\\5\37\20\2\\]\5A!\2]^\5+\26\2^_\5\67\34\2_"+
		"`\5\65\33\2`\f\3\2\2\2ab\59\35\2bc\5=\37\2cd\5\67\34\2de\5-\27\2ef\5#"+
		"\22\2fg\5\37\20\2gh\5A!\2hi\5+\26\2ij\5\67\34\2jk\5\65\33\2k\16\3\2\2"+
		"\2lm\5-\27\2mn\5\67\34\2no\5+\26\2op\5\65\33\2p\20\3\2\2\2qr\5\37\20\2"+
		"rs\5\33\16\2st\5=\37\2tu\5A!\2uv\5#\22\2vw\5? \2wx\5+\26\2xy\5\33\16\2"+
		"yz\5\65\33\2z\22\3\2\2\2{\177\7)\2\2|~\13\2\2\2}|\3\2\2\2~\u0081\3\2\2"+
		"\2\177\u0080\3\2\2\2\177}\3\2\2\2\u0080\u0082\3\2\2\2\u0081\177\3\2\2"+
		"\2\u0082\u0083\7)\2\2\u0083\24\3\2\2\2\u0084\u0088\7]\2\2\u0085\u0087"+
		"\13\2\2\2\u0086\u0085\3\2\2\2\u0087\u008a\3\2\2\2\u0088\u0089\3\2\2\2"+
		"\u0088\u0086\3\2\2\2\u0089\u008b\3\2\2\2\u008a\u0088\3\2\2\2\u008b\u008c"+
		"\7_\2\2\u008c\26\3\2\2\2\u008d\u0091\t\2\2\2\u008e\u0090\t\3\2\2\u008f"+
		"\u008e\3\2\2\2\u0090\u0093\3\2\2\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2"+
		"\2\2\u0092\30\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0096\t\4\2\2\u0095\u0094"+
		"\3\2\2\2\u0096\u0097\3\2\2\2\u0097\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098"+
		"\u0099\3\2\2\2\u0099\u009a\b\r\2\2\u009a\32\3\2\2\2\u009b\u009c\t\5\2"+
		"\2\u009c\34\3\2\2\2\u009d\u009e\t\6\2\2\u009e\36\3\2\2\2\u009f\u00a0\t"+
		"\7\2\2\u00a0 \3\2\2\2\u00a1\u00a2\t\b\2\2\u00a2\"\3\2\2\2\u00a3\u00a4"+
		"\t\t\2\2\u00a4$\3\2\2\2\u00a5\u00a6\t\n\2\2\u00a6&\3\2\2\2\u00a7\u00a8"+
		"\t\13\2\2\u00a8(\3\2\2\2\u00a9\u00aa\t\f\2\2\u00aa*\3\2\2\2\u00ab\u00ac"+
		"\t\r\2\2\u00ac,\3\2\2\2\u00ad\u00ae\t\16\2\2\u00ae.\3\2\2\2\u00af\u00b0"+
		"\t\17\2\2\u00b0\60\3\2\2\2\u00b1\u00b2\t\20\2\2\u00b2\62\3\2\2\2\u00b3"+
		"\u00b4\t\21\2\2\u00b4\64\3\2\2\2\u00b5\u00b6\t\22\2\2\u00b6\66\3\2\2\2"+
		"\u00b7\u00b8\t\23\2\2\u00b88\3\2\2\2\u00b9\u00ba\t\24\2\2\u00ba:\3\2\2"+
		"\2\u00bb\u00bc\t\25\2\2\u00bc<\3\2\2\2\u00bd\u00be\t\26\2\2\u00be>\3\2"+
		"\2\2\u00bf\u00c0\t\27\2\2\u00c0@\3\2\2\2\u00c1\u00c2\t\30\2\2\u00c2B\3"+
		"\2\2\2\u00c3\u00c4\t\31\2\2\u00c4D\3\2\2\2\u00c5\u00c6\t\32\2\2\u00c6"+
		"F\3\2\2\2\u00c7\u00c8\t\33\2\2\u00c8H\3\2\2\2\u00c9\u00ca\t\34\2\2\u00ca"+
		"J\3\2\2\2\u00cb\u00cc\t\35\2\2\u00ccL\3\2\2\2\u00cd\u00ce\t\36\2\2\u00ce"+
		"N\3\2\2\2\7\2\177\u0088\u0091\u0097\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}