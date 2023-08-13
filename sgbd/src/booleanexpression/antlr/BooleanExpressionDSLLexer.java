package booleanexpression.antlr;// Generated from BooleanExpressionDSL.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class BooleanExpressionDSLLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, REL_OP=4, OR=5, AND=6, DECIMAL=7, INTEGER=8, IDENTIFIER=9, 
		LETTER=10, STRING=11, DIGIT=12, WS=13;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "REL_OP", "OR", "AND", "DECIMAL", "INTEGER", 
			"IDENTIFIER", "LETTER", "STRING", "DIGIT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "'!'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, "REL_OP", "OR", "AND", "DECIMAL", "INTEGER", 
			"IDENTIFIER", "LETTER", "STRING", "DIGIT", "WS"
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


	public BooleanExpressionDSLLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "BooleanExpressionDSL.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\17\u00a5\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5"+
		"\58\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6D\n\6\3\7\3\7\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\7\3\7\5\7`\n\7\3\b\6\bc\n\b\r\b\16\bd\3\b\3\b\7\bi\n\b"+
		"\f\b\16\bl\13\b\3\t\6\to\n\t\r\t\16\tp\3\n\3\n\5\nu\n\n\3\n\3\n\3\n\7"+
		"\nz\n\n\f\n\16\n}\13\n\3\n\3\n\3\n\5\n\u0082\n\n\3\n\3\n\3\n\7\n\u0087"+
		"\n\n\f\n\16\n\u008a\13\n\5\n\u008c\n\n\3\13\6\13\u008f\n\13\r\13\16\13"+
		"\u0090\3\f\3\f\3\f\7\f\u0096\n\f\f\f\16\f\u0099\13\f\3\f\3\f\3\r\3\r\3"+
		"\16\6\16\u00a0\n\16\r\16\16\16\u00a1\3\16\3\16\2\2\17\3\3\5\4\7\5\t\6"+
		"\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\3\2\b\5\2>>@@\u2267\u2267"+
		"\4\2C\\c|\3\2))\4\2\f\f\17\17\3\2\62;\5\2\13\f\17\17\"\"\2\u00c9\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3"+
		"\2\2\2\2\33\3\2\2\2\3\35\3\2\2\2\5\37\3\2\2\2\7!\3\2\2\2\t\67\3\2\2\2"+
		"\13C\3\2\2\2\r_\3\2\2\2\17b\3\2\2\2\21n\3\2\2\2\23t\3\2\2\2\25\u008e\3"+
		"\2\2\2\27\u0092\3\2\2\2\31\u009c\3\2\2\2\33\u009f\3\2\2\2\35\36\7*\2\2"+
		"\36\4\3\2\2\2\37 \7+\2\2 \6\3\2\2\2!\"\7#\2\2\"\b\3\2\2\2#$\7?\2\2$8\7"+
		"?\2\2%8\7?\2\2&\'\7#\2\2\'8\7?\2\2(8\7\u2262\2\2)*\7>\2\2*8\7?\2\2+8\7"+
		"\u2266\2\2,-\7@\2\2-8\7?\2\2.8\t\2\2\2/\60\7k\2\2\608\7u\2\2\61\62\7k"+
		"\2\2\62\63\7u\2\2\63\64\7\"\2\2\64\65\7p\2\2\65\66\7q\2\2\668\7v\2\2\67"+
		"#\3\2\2\2\67%\3\2\2\2\67&\3\2\2\2\67(\3\2\2\2\67)\3\2\2\2\67+\3\2\2\2"+
		"\67,\3\2\2\2\67.\3\2\2\2\67/\3\2\2\2\67\61\3\2\2\28\n\3\2\2\29:\7~\2\2"+
		":D\7~\2\2;<\7Q\2\2<D\7T\2\2=>\7Q\2\2>D\7t\2\2?@\7q\2\2@D\7T\2\2AB\7q\2"+
		"\2BD\7t\2\2C9\3\2\2\2C;\3\2\2\2C=\3\2\2\2C?\3\2\2\2CA\3\2\2\2D\f\3\2\2"+
		"\2EF\7(\2\2F`\7(\2\2GH\7C\2\2HI\7P\2\2I`\7F\2\2JK\7C\2\2KL\7p\2\2L`\7"+
		"f\2\2MN\7C\2\2NO\7p\2\2O`\7F\2\2PQ\7C\2\2QR\7P\2\2R`\7f\2\2ST\7c\2\2T"+
		"U\7P\2\2U`\7F\2\2VW\7c\2\2WX\7P\2\2X`\7f\2\2YZ\7c\2\2Z[\7p\2\2[`\7F\2"+
		"\2\\]\7c\2\2]^\7p\2\2^`\7f\2\2_E\3\2\2\2_G\3\2\2\2_J\3\2\2\2_M\3\2\2\2"+
		"_P\3\2\2\2_S\3\2\2\2_V\3\2\2\2_Y\3\2\2\2_\\\3\2\2\2`\16\3\2\2\2ac\5\31"+
		"\r\2ba\3\2\2\2cd\3\2\2\2db\3\2\2\2de\3\2\2\2ef\3\2\2\2fj\7\60\2\2gi\5"+
		"\31\r\2hg\3\2\2\2il\3\2\2\2jh\3\2\2\2jk\3\2\2\2k\20\3\2\2\2lj\3\2\2\2"+
		"mo\5\31\r\2nm\3\2\2\2op\3\2\2\2pn\3\2\2\2pq\3\2\2\2q\22\3\2\2\2ru\5\25"+
		"\13\2su\7a\2\2tr\3\2\2\2ts\3\2\2\2u{\3\2\2\2vz\5\25\13\2wz\5\31\r\2xz"+
		"\7a\2\2yv\3\2\2\2yw\3\2\2\2yx\3\2\2\2z}\3\2\2\2{y\3\2\2\2{|\3\2\2\2|\u008b"+
		"\3\2\2\2}{\3\2\2\2~\u0081\7\60\2\2\177\u0082\5\25\13\2\u0080\u0082\7a"+
		"\2\2\u0081\177\3\2\2\2\u0081\u0080\3\2\2\2\u0082\u0088\3\2\2\2\u0083\u0087"+
		"\5\25\13\2\u0084\u0087\5\31\r\2\u0085\u0087\7a\2\2\u0086\u0083\3\2\2\2"+
		"\u0086\u0084\3\2\2\2\u0086\u0085\3\2\2\2\u0087\u008a\3\2\2\2\u0088\u0086"+
		"\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u008c\3\2\2\2\u008a\u0088\3\2\2\2\u008b"+
		"~\3\2\2\2\u008b\u008c\3\2\2\2\u008c\24\3\2\2\2\u008d\u008f\t\3\2\2\u008e"+
		"\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2"+
		"\2\2\u0091\26\3\2\2\2\u0092\u0097\7)\2\2\u0093\u0096\n\4\2\2\u0094\u0096"+
		"\t\5\2\2\u0095\u0093\3\2\2\2\u0095\u0094\3\2\2\2\u0096\u0099\3\2\2\2\u0097"+
		"\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u009a\3\2\2\2\u0099\u0097\3\2"+
		"\2\2\u009a\u009b\7)\2\2\u009b\30\3\2\2\2\u009c\u009d\t\6\2\2\u009d\32"+
		"\3\2\2\2\u009e\u00a0\t\7\2\2\u009f\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1"+
		"\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\b\16"+
		"\2\2\u00a4\34\3\2\2\2\24\2\67C_djpty{\u0081\u0086\u0088\u008b\u0090\u0095"+
		"\u0097\u00a1\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}