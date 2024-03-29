package dsl.antlr4;// Generated from RelAlgebra.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RelAlgebraLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, IMPORT=22, AS=23, RELATION=24, 
		PREDICATE=25, DIGIT=26, PATH=27, THIS=28, WS=29;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"T__17", "T__18", "T__19", "T__20", "IMPORT", "AS", "RELATION", "PREDICATE", 
			"DIGIT", "PATH", "THIS", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'.head'", "'='", "'<'", "','", "'>'", "'selection'", "'projection'", 
			"'sort'", "'group'", "'rename'", "'aggregation'", "'indexer'", "'join'", 
			"'leftJoin'", "'rightJoin'", "'cartesianProduct'", "'union'", "'intersection'", 
			"'('", "')'", "'import'", "'as'", null, null, null, null, "'this.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, "IMPORT", 
			"AS", "RELATION", "PREDICATE", "DIGIT", "PATH", "THIS", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\37\u00fa\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3"+
		"\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3"+
		"\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\7\31\u00d5\n\31\f\31"+
		"\16\31\u00d8\13\31\3\32\3\32\7\32\u00dc\n\32\f\32\16\32\u00df\13\32\3"+
		"\32\3\32\3\33\3\33\3\34\3\34\7\34\u00e7\n\34\f\34\16\34\u00ea\13\34\3"+
		"\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\3\36\6\36\u00f5\n\36\r\36\16\36"+
		"\u00f6\3\36\3\36\3\u00dd\2\37\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13"+
		"\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61"+
		"\32\63\33\65\34\67\359\36;\37\3\2\7\4\2C\\c|\6\2\62;C\\aac|\3\2\62;\6"+
		"\2\61;C\\aac|\5\2\13\f\17\17\"\"\2\u00fd\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3"+
		"\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2"+
		"\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35"+
		"\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)"+
		"\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2"+
		"\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\3=\3\2\2\2\5?\3\2\2\2\7"+
		"E\3\2\2\2\tG\3\2\2\2\13I\3\2\2\2\rK\3\2\2\2\17M\3\2\2\2\21W\3\2\2\2\23"+
		"b\3\2\2\2\25g\3\2\2\2\27m\3\2\2\2\31t\3\2\2\2\33\u0080\3\2\2\2\35\u0088"+
		"\3\2\2\2\37\u008d\3\2\2\2!\u0096\3\2\2\2#\u00a0\3\2\2\2%\u00b1\3\2\2\2"+
		"\'\u00b7\3\2\2\2)\u00c4\3\2\2\2+\u00c6\3\2\2\2-\u00c8\3\2\2\2/\u00cf\3"+
		"\2\2\2\61\u00d2\3\2\2\2\63\u00d9\3\2\2\2\65\u00e2\3\2\2\2\67\u00e4\3\2"+
		"\2\29\u00ed\3\2\2\2;\u00f4\3\2\2\2=>\7=\2\2>\4\3\2\2\2?@\7\60\2\2@A\7"+
		"j\2\2AB\7g\2\2BC\7c\2\2CD\7f\2\2D\6\3\2\2\2EF\7?\2\2F\b\3\2\2\2GH\7>\2"+
		"\2H\n\3\2\2\2IJ\7.\2\2J\f\3\2\2\2KL\7@\2\2L\16\3\2\2\2MN\7u\2\2NO\7g\2"+
		"\2OP\7n\2\2PQ\7g\2\2QR\7e\2\2RS\7v\2\2ST\7k\2\2TU\7q\2\2UV\7p\2\2V\20"+
		"\3\2\2\2WX\7r\2\2XY\7t\2\2YZ\7q\2\2Z[\7l\2\2[\\\7g\2\2\\]\7e\2\2]^\7v"+
		"\2\2^_\7k\2\2_`\7q\2\2`a\7p\2\2a\22\3\2\2\2bc\7u\2\2cd\7q\2\2de\7t\2\2"+
		"ef\7v\2\2f\24\3\2\2\2gh\7i\2\2hi\7t\2\2ij\7q\2\2jk\7w\2\2kl\7r\2\2l\26"+
		"\3\2\2\2mn\7t\2\2no\7g\2\2op\7p\2\2pq\7c\2\2qr\7o\2\2rs\7g\2\2s\30\3\2"+
		"\2\2tu\7c\2\2uv\7i\2\2vw\7i\2\2wx\7t\2\2xy\7g\2\2yz\7i\2\2z{\7c\2\2{|"+
		"\7v\2\2|}\7k\2\2}~\7q\2\2~\177\7p\2\2\177\32\3\2\2\2\u0080\u0081\7k\2"+
		"\2\u0081\u0082\7p\2\2\u0082\u0083\7f\2\2\u0083\u0084\7g\2\2\u0084\u0085"+
		"\7z\2\2\u0085\u0086\7g\2\2\u0086\u0087\7t\2\2\u0087\34\3\2\2\2\u0088\u0089"+
		"\7l\2\2\u0089\u008a\7q\2\2\u008a\u008b\7k\2\2\u008b\u008c\7p\2\2\u008c"+
		"\36\3\2\2\2\u008d\u008e\7n\2\2\u008e\u008f\7g\2\2\u008f\u0090\7h\2\2\u0090"+
		"\u0091\7v\2\2\u0091\u0092\7L\2\2\u0092\u0093\7q\2\2\u0093\u0094\7k\2\2"+
		"\u0094\u0095\7p\2\2\u0095 \3\2\2\2\u0096\u0097\7t\2\2\u0097\u0098\7k\2"+
		"\2\u0098\u0099\7i\2\2\u0099\u009a\7j\2\2\u009a\u009b\7v\2\2\u009b\u009c"+
		"\7L\2\2\u009c\u009d\7q\2\2\u009d\u009e\7k\2\2\u009e\u009f\7p\2\2\u009f"+
		"\"\3\2\2\2\u00a0\u00a1\7e\2\2\u00a1\u00a2\7c\2\2\u00a2\u00a3\7t\2\2\u00a3"+
		"\u00a4\7v\2\2\u00a4\u00a5\7g\2\2\u00a5\u00a6\7u\2\2\u00a6\u00a7\7k\2\2"+
		"\u00a7\u00a8\7c\2\2\u00a8\u00a9\7p\2\2\u00a9\u00aa\7R\2\2\u00aa\u00ab"+
		"\7t\2\2\u00ab\u00ac\7q\2\2\u00ac\u00ad\7f\2\2\u00ad\u00ae\7w\2\2\u00ae"+
		"\u00af\7e\2\2\u00af\u00b0\7v\2\2\u00b0$\3\2\2\2\u00b1\u00b2\7w\2\2\u00b2"+
		"\u00b3\7p\2\2\u00b3\u00b4\7k\2\2\u00b4\u00b5\7q\2\2\u00b5\u00b6\7p\2\2"+
		"\u00b6&\3\2\2\2\u00b7\u00b8\7k\2\2\u00b8\u00b9\7p\2\2\u00b9\u00ba\7v\2"+
		"\2\u00ba\u00bb\7g\2\2\u00bb\u00bc\7t\2\2\u00bc\u00bd\7u\2\2\u00bd\u00be"+
		"\7g\2\2\u00be\u00bf\7e\2\2\u00bf\u00c0\7v\2\2\u00c0\u00c1\7k\2\2\u00c1"+
		"\u00c2\7q\2\2\u00c2\u00c3\7p\2\2\u00c3(\3\2\2\2\u00c4\u00c5\7*\2\2\u00c5"+
		"*\3\2\2\2\u00c6\u00c7\7+\2\2\u00c7,\3\2\2\2\u00c8\u00c9\7k\2\2\u00c9\u00ca"+
		"\7o\2\2\u00ca\u00cb\7r\2\2\u00cb\u00cc\7q\2\2\u00cc\u00cd\7t\2\2\u00cd"+
		"\u00ce\7v\2\2\u00ce.\3\2\2\2\u00cf\u00d0\7c\2\2\u00d0\u00d1\7u\2\2\u00d1"+
		"\60\3\2\2\2\u00d2\u00d6\t\2\2\2\u00d3\u00d5\t\3\2\2\u00d4\u00d3\3\2\2"+
		"\2\u00d5\u00d8\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\62"+
		"\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d9\u00dd\7]\2\2\u00da\u00dc\13\2\2\2\u00db"+
		"\u00da\3\2\2\2\u00dc\u00df\3\2\2\2\u00dd\u00de\3\2\2\2\u00dd\u00db\3\2"+
		"\2\2\u00de\u00e0\3\2\2\2\u00df\u00dd\3\2\2\2\u00e0\u00e1\7_\2\2\u00e1"+
		"\64\3\2\2\2\u00e2\u00e3\t\4\2\2\u00e3\66\3\2\2\2\u00e4\u00e8\7\61\2\2"+
		"\u00e5\u00e7\t\5\2\2\u00e6\u00e5\3\2\2\2\u00e7\u00ea\3\2\2\2\u00e8\u00e6"+
		"\3\2\2\2\u00e8\u00e9\3\2\2\2\u00e9\u00eb\3\2\2\2\u00ea\u00e8\3\2\2\2\u00eb"+
		"\u00ec\t\3\2\2\u00ec8\3\2\2\2\u00ed\u00ee\7v\2\2\u00ee\u00ef\7j\2\2\u00ef"+
		"\u00f0\7k\2\2\u00f0\u00f1\7u\2\2\u00f1\u00f2\7\60\2\2\u00f2:\3\2\2\2\u00f3"+
		"\u00f5\t\6\2\2\u00f4\u00f3\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f4\3\2"+
		"\2\2\u00f6\u00f7\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00f9\b\36\2\2\u00f9"+
		"<\3\2\2\2\7\2\u00d6\u00dd\u00e8\u00f6\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}