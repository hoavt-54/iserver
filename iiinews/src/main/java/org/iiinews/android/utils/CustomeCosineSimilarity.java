package org.iiinews.android.utils;

/**
 * SimMetrics - SimMetrics is a java library of Similarity or Distance
 * Metrics, e.g. Levenshtein Distance, that provide float based similarity
 * measures between String Data. All metrics return consistant measures
 * rather than unbounded similarity scores.
 *
 * Copyright (C) 2005 Sam Chapman - Open Source Release v1.1
 *
 * Please Feel free to contact me about this library, I would appreciate
 * knowing quickly what you wish to use it for and any criticisms/comments
 * upon the SimMetric library.
 *
 * email:       s.chapman@dcs.shef.ac.uk
 * www:         http://www.dcs.shef.ac.uk/~sam/
 * www:         http://www.dcs.shef.ac.uk/~sam/stringmetrics.html
 *
 * address:     Sam Chapman,
 *              Department of Computer Science,
 *              University of Sheffield,
 *              Sheffield,
 *              S. Yorks,
 *              S1 4DP
 *              United Kingdom,
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Package: uk.ac.shef.wit.simmetrics.similaritymetrics.cosinesimilarity
 * Description: uk.ac.shef.wit.simmetrics.similaritymetrics.cosinesimilarity implements a

 * Date: 05-Apr-2004
 * Time: 10:52:58
 * @author Sam Chapman <a href="http://www.dcs.shef.ac.uk/~sam/">Website</a>, <a href="mailto:sam@dcs.shef.ac.uk">Email</a>.
 * @version 1.1
 */
public final class CustomeCosineSimilarity {

    /**
     * a constant for calculating the estimated timing cost.
     */
    private final float ESTIMATEDTIMINGCONST = 0.00000038337142857142857142857142857142f;

    /**
     * constructor - default (empty).
     */
    public CustomeCosineSimilarity() {
    }


    public String getShortDescriptionString() {
        return "CosineSimilarity";
    }

    /**
     * returns the long string identifier for the metric.
     *
     * @return the long string identifier for the metric
     */
    public String getLongDescriptionString() {
        return "Implements the Cosine Similarity algorithm providing a similarity measure between two strings from the angular divergence within term based vector space";
    }

    /**
     * gets a div class xhtml similarity explaining the operation of the metric.
     *
     * @param string1 string 1
     * @param string2 string 2
     *
     * @return a div class html section detailing the metric operation.
     */
    public String getSimilarityExplained(String string1, String string2) {
        //todo this should explain the operation of a given comparison
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * gets the estimated time in milliseconds it takes to perform a similarity timing.
     *
     * @param string1 string 1
     * @param string2 string 2
     *
     * @return the estimated time in milliseconds taken to perform the similarity measure
     */
    public float getSimilarityTimingEstimated(final String string1, final String string2) {
        //timed millisecond times with string lengths from 1 + 50 each increment
        //0     0.02    0.03    0.05    0.08    0.11    0.14    0.18    0.23    0.27    0.33    0.39    0.46    0.52    0.6     0.65    0.76    0.84    0.94    1.01    1.13    1.23    1.49    1.45    1.95    1.67    2.26    1.93    2.6     2.26    2.86    2.54    3.17    2.91    3.76    3.17    3.9     3.5     4.32    3.9     5.1     4.32    5.64    4.83    5.64    5.07    6.34    5.64    7.03    5.97    7.81    6.55    8.12    7       9.23    7.52    9.71    8.12    10.68   8.46
        final float str1Length = string1.length();
        final float str2Length = string2.length();
        return (str1Length + str2Length) * ((str1Length + str2Length) * ESTIMATEDTIMINGCONST);
    }
    
    
    public static boolean isValidWord (String word){
    	for (char c : word.toCharArray()) { 
    	    if(!Character.isLetterOrDigit(c))
    	    {
    	        return false;
    	    }
    	}
    	return true;
    }
    
    /**
     * gets the similarity of the two strings using CosineSimilarity.
     *
     * @param string1
     * @param string2
     * @return a value between 0-1 of the similarity
     */
    public static float getSimilarity(final String string1, final String string2) {
    	if (string1 == null || string1.length() < 300 || string2 == null || string2.length() < 300)
    		return 0;
    	short s1 = 1;
    	final HashMap<String, Short> str1TokensMap = new HashMap<>();
    	for (String token : string1.split(" ")){
    		if (isValidWord(token)){
				Short count = str1TokensMap.get(token);
				if (count == null)
					str1TokensMap.put(token, s1);
				else
					str1TokensMap.put(token, ++count);
    		}
    	}
    	final HashMap<String, Short> str2TokensMap = new HashMap<>();
    	for (String token : string2.split(" ")){
    		if (isValidWord(token)){
	    		Short count = str2TokensMap.get(token);
	    		if (count == null)
					str2TokensMap.put(token, s1);
				else
					str2TokensMap.put(token, ++count);
    		}
    	}
    	
        final Set<String> mutualTokens = new HashSet<String>();
        mutualTokens.addAll(str1TokensMap.keySet());
        mutualTokens.retainAll(str2TokensMap.keySet());
        
        float multiple = 0; //A.B
        float module1 = 0; //|A|
        float module2 = 0; //|B|
        for (String element : str1TokensMap.keySet()){
        	Short freq1 = str1TokensMap.get(element);
        	module1 += freq1 * freq1;
        }
        
        for (String element : str2TokensMap.keySet()){
        	Short freq = str2TokensMap.get(element);
        	module2 += freq * freq;
        }
        
        for (String element : mutualTokens){
        	Short freq1 = str1TokensMap.get(element);
        	Short freq2 = str2TokensMap.get(element);
        	multiple += freq1 * freq2;
        }
        float multiModule =  (float) (Math.sqrt(module1) * Math.sqrt(module2));
        if ( multiModule == 0 ) return 0;
        
        return (float) (multiple/multiModule);
        
    }

    /**
     * gets the un-normalised similarity measure of the metric for the given strings.
     *
     * @param string1
     * @param string2
     * @return returns the score of the similarity measure (un-normalised)
     */
    public float getUnNormalisedSimilarity(String string1, String string2) {
        float simil =  getSimilarity(string1, string2);
        System.out.println(simil);
        return simil;
    }
    
    public static void main(String[] args) {
		String string1 = "bristol palin , reality tv personality daughter 2008 republican vice presidential candidate sarah palin , engaged marry medal honor recipient dakota meyer , couple saturday `` night , sgt dakota meyer proposed ! '' palin , 24 , blog meyer , 26 , awarded medal honor , highest u.s. award bravery , 2011 saving 36 comrades lives ambush afghanistan 2009. bristol palin meyer 2014 alaska film reality tv show `` amazing america '' mother , governor alaska election running mate republican presidential nominee john mccain meyer facebook , `` wait spend life tripp , '' bristol palin fathered high school boyfriend facebook page , sarah palin , `` families happier bristol dakota ! honored dakota family '' social — reuters published march 14 2015 , 7:10 pm ";
		
		
		
		
		
		
		
		
		
		
		String string2 = "bristol palin speaks panel discussion disney-abc television group portion television critics association summer press beverly hills , california , file photo july 27 , 2012. reuters anchorage , alaska -- bristol palin engaged medal honor recipient dakota meyer daughter alaska governor gop vice presidential candidate sarah palin blog post saturday meyer alaska film `` amazing america '' reality show mother year `` wonderful [ palin ] tripp proud marrying , '' meyer , adding proposal happened rascal flatts concert palin posted photo meyer instagram announce news luckiest girl world , wait marry ! ! ! ! ❤️❤️❤️ photo posted bristol palin ( @ bsmp2 ) mar 13 , 2015 11:50pm pdt meyer awarded medal honor 2011. kentucky native credited saving lives 36 marines , army soldiers afghan soldiers providing cover find firefight taliban sarah palin posted link daughter blog post verified facebook account `` honored dakota family '' `` afghanistan happy , lot feeling , '' meyer wrote saturday facebook account `` life live live , bristol '' palin family attorney john tiemessen return email voicemail message press seeking comment ";
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//AbstractStringMetric metric = new CosineSimilarity();
		//1 -2 = 0.3211
		//1- 3 = 0.23466162
		
		// 1 - 2 = 
		System.out.println(getSimilarity(string1, string2));//33744407382756847
		/*Counter({'Nemtsov': 27, 'Putin': 21, 'Russian': 17, 'Russia': 14, 'I': 11, 'opposition': 9, 'Kremlin': 9, 'Boris': 8, 'President': 7, 'people': 7, 'It': 7, 'He': 6, 'A': 6, 'Baker': 6, 'Moscow': 6, 'Kasparov': 6, 'CNN': 5, 'In': 5, 'political': 5, 'message': 5, 'country': 4, 'killed': 4, 'Ukraine': 4, 'interview': 4, 'killing': 4, 'The': 4, 'leader': 4, 'Yashin': 3, 'told': 3, 'Friday': 3, 'year': 3, 'Garry': 3, 'critic': 3, 'business': 3, 'afraid': 3, 'death': 3, 'corruption': 3, '2014': 3, 'Saturday': 3, 'murder': 3, 'number': 3, 'But': 3, 'bit': 3, 'When': 3, 'enemies': 3, 'friend': 3, 'authorities': 3, 'added': 3, 'rally': 3, 'hatred': 3, 'prerequisite': 2, 'walking': 2, '19': 2, 'party': 2, 'Times': 2, '24': 2, 'York': 2, 'today': 2, 'atmosphere': 2, 'hasty': 2, 'critics': 2, 'Opposition': 2, 'century': 2, 'Itar': 2, 'shooting': 2, 'life': 2, 'If': 2, 'fighting': 2, 'contract': 2, 'Critics': 2, 'Poroshenko': 2, '2006': 2, 'show': 2, 'government': 2, 'statement': 2, 'situation': 2, 'power': 2, 'Sunday': 2, 'Party': 2, 'bloodshed': 2, 'loyalty': 2, 'overt': 2, 'Olympics': 2, 'Ioffe': 2, 'violence': 2, 'put': 2, 'held': 2, 'chance': 2, 'prison': 2, 'extensively': 2, 'shot': 2, 'vocal': 2, 'car': 2, 'night': 2, 'Tass': 2, 'work': 2, 'report': 2, 'fierce': 2, 'argued': 2, 'justice': 2, 'opened': 2, 'This': 2, 'years': 2, '2015': 2, 'reported': 2, 'February': 2, 'Kasparov63': 2, 'People': 2, 'mourning': 2, 'pulled': 2, 'Now': 2, 'Obama': 2, 'bridge': 2, 'speaking': 2, 'court': 2, 'live': 2, 'accused': 2, '27': 2, 'Sochi': 2, 'march': 2, 'They': 2, 'RIP': 2, 'team': 2, 'elections': 2, 'Power': 2, 'state': 2, 'New': 2, 'fellow': 2, 'protested': 2, 'tweeted': 2, 'call': 1, 'published': 1, 'propaganda': 1, 'high': 1, 'decided': 1, 'contributing': 1, 'discord': 1, 'literally': 1, '02': 1, 'arrested': 1, 'cold': 1, 'remember': 1, 'planned': 1, 'Investigators': 1, 'quickly': 1, 'Anna': 1, '2012': 1, 'leaders': 1, 'journalist': 1, 'suspicion': 1, 'spending': 1, 'spy': 1, 'elected': 1, 'fears': 1, 'noted': 1, 'Ukrainian': 1, 'suffered': 1, 'Videos': 1, 'lying': 1, 'experts': 1, 'Prokhorov': 1, 'condemned': 1, 'advocate': 1, 'Committee': 1, 'person': 1, 'caught': 1, 'candid': 1, 'London': 1, 'AC360': 1, 'Politkovskaya': 1, 'speculation': 1, 'targeted': 1, 'parliamentary': 1, 'send': 1, 'companion': 1, 'official': 1, 'personally': 1, 'struggle': 1, 'organizing': 1, 'true': 1, 'Watched': 1, 'force': 1, '1990s': 1, 'walk': 1, 'Who': 1, 'acknowledging': 1, 'swindles': 1, 'killings': 1, 'There': 1, 'bodyguards': 1, 'destroy': 1, 'dedication': 1, 'called': 1, 'law': 1, 'radioactive': 1, 'Republican': 1, 'Khodorkovsky': 1, 'Winter': 1, 'mother': 1, 'criminal': 1, 'tens': 1, 'willingness': 1, 'speak': 1, 'Peskov': 1, 'world': 1, 'direction': 1, 'views': 1, 'politics': 1, 'reporter': 1, 'citizens': 1, 'weapons': 1, 'putting': 1, 'authority': 1, 'good': 1, 'Julia': 1, 'reports': 1, 'coup': 1, 'died': 1, 'radio': 1, 'deceased': 1, 'critical': 1, 'After': 1, 'trouble': 1, 'education': 1, 'troops': 1, 'lawyer': 1, 'strong': 1, '60': 1, 'security': 1, 'Dmitry': 1, 'switched': 1, 'written': 1, 'involved': 1, 'Has': 1, 'fingers': 1, 'task': 1, 'results': 1, 'large': 1, 'Vadim': 1, 'Even': 1, 'Last': 1, 'order': 1, 'White': 1, 'threatened': 1, 'top': 1, 'Ilya': 1, 'hold': 1, 'criticized': 1, 'His': 1, 'chess': 1, 'decline': 1, 'press': 1, 'city': 1, 'optimism': 1, 'support': 1, 'difficult': 1, 'outrageous': 1, 'tireless': 1, 'thousands': 1, 'Fierce': 1, 'covered': 1, 'entitled': 1, 'That': 1, 'working': 1, 'downtown': 1, 'billion': 1, 'return': 1, 'services': 1, 'committee': 1, 'earlier': 1, '2013': 1, 'dose': 1, 'understand': 1, 'turn': 1, '2009': 1, 'culture': 1, 'Rising': 1, 'criticism': 1, 'killer': 1, 'handling': 1, 'newspaper': 1, 'spokesman': 1, 'Sooner': 1, 'highlights': 1, 'central': 1, 'games': 1, 'History': 1, 'polonium': 1, 'Twitter': 1, 'Story': 1, 'Yeltsin': 1, 'We': 1, 'rights': 1, 'midnight': 1, 'embezzled': 1, 'No': 1, 'family': 1, 'murderers': 1, 'ramifications': 1, 'crisis': 1, 'investigative': 1, 'Fired': 1, 'blame': 1, 'suspected': 1, 'Calls': 1, 'bravery': 1, 'risked': 1, 'You': 1, 'S': 1, 'regime': 1, 'responsible': 1, 'miserable': 1, 'condolences': 1, 'wound': 1, 'month': 1, 'center': 1, 'connection': 1, 'weather': 1, 'constantly': 1, 'camps': 1, 'cars': 1, 'find': 1, 'unsafe': 1, 'corrupt': 1, 'lamented': 1, 'appreciated': 1, 'Rest': 1, 'terrible': 1, 'perceive': 1, 'Petro': 1, 'owners': 1, 'agency': 1, 'champion': 1, 'element': 1, 'involvement': 1, 'writer': 1, 'Federation': 1, 'fire': 1, 'House': 1, 'supporter': 1, 'offer': 1, 'heading': 1, 'enforcement': 1, 'white': 1, 'circumstances': 1, 'final': 1, 'investigation': 1, 'marginalized': 1, 'turned': 1, 'deputy': 1, 'Everybody': 1, 'prime': 1, 'fact': 1, 'praised': 1, 'restaurant': 1, 'Investigative': 1, 'case': 1, 'kill': 1, 'Mikhail': 1, 'Litvinenko': 1, 'impartial': 1, 'replay': 1, 'dictatorship': 1, 'Peter': 1, 'pointed': 1, '10': 1, 'conspiracy': 1, '30': 1, 'Barack': 1, 'Anthony': 1, 'trafficking': 1, 'labor': 1, 'wounded': 1, 'minister': 1, 'author': 1, 'observed': 1, 'Vladimir': 1, 'gave': 1, 'courageous': 1, 'thinks': 1, 'Bourdain': 1, 'lethal': 1, 'talking': 1, 'activist': 1, 'investigate': 1, 'arrests': 1, 'busy': 1, 'expressed': 1, 'prompting': 1, 'share': 1, 'free': 1, 'Sobesednik': 1, 'peace': 1, 'Alexander': 1, 'More': 1, 'powerful': 1, 'wet': 1, 'demises': 1, 'poisoned': 1, 'cost': 1, 'point': 1, 'Magazine': 1, 'agencies': 1, 'doubt': 1, 'And': 1, 'backdrop': 1, 'suspicious': 1, 'profile': 1, 'What': 1, 'figures': 1, 'describing': 1, 'World': 1, 'scene': 1, 'ordered': 1, 'occasions': 1, 'group': 1, 'directed': 1, 'series': 1, 'rule': 1, 'wrote': 1, 'latest': 1, 'terms': 1, 'layers': 1, 'claimed': 1, 'female': 1, 'Freedom': 1, 'admired': 1, 'fear': 1, 'weeks': 1, '2011': 1, 'encouraged': 1, 'chances': 1, 'sentenced': 1, 'history': 1, 'multiple': 1, 'social': 1, 'Just': 1, '55': 1, 'brought': 1, 'promoting': 1, 'Oligarch': 1, 'seeking': 1, 'crew': 1, 'news': 1, 'policies': 1, 'U': 1, '7': 1, 'liberal': 1})
		Counter({'Nemtsov': 16, 'opposition': 12, 'Russia': 11, 'Putin': 7, 'prominent': 6, 'He': 6, 'Russian': 6, 'Yeltsin': 5, 'election': 5, 'Boris': 5, '2011': 5, 'In': 4, 'Mr': 4, 'SPS': 4, 'parliament': 4, 'member': 3, 'economic': 3, 'critic': 3, 'liberal': 3, 'eye': 2, 'supporters': 2, 'crisis': 2, 'vocal': 2, 'years': 2, 'movement': 2, 'But': 2, 'liberals': 2, 'detained': 2, 'considered': 2, 'politics': 2, 'Vladimir': 2, 'party': 2, 'Ukraine': 2, 'longer': 2, 'president': 2, 'founded': 2, 'Party': 2, 'Moscow': 2, 'Kasparov': 2, 'decline': 2, 'President': 2, 'Novgorod': 2, 'fellow': 2, 'Nizhny': 2, '2003': 2, 'scared': 1, 'recordings': 1, 'violence': 1, 'elected': 1, 'to': 1, 'charismatic': 1, 'Stories': 1, 'Olympic': 1, 'presidential': 1, '1991': 1, 'arrested': 1, '1990': 1, 'shot': 1, 'scientist': 1, 'annexed': 1, 'marches': 1, 'country': 1, 'Despite': 1, 'Crimea': 1, 'attitude': 1, 'planned': 1, 'Profile': 1, '2012': 1, 'open': 1, 'Freedom': 1, '15': 1, 'failed': 1, 'message': 1, 'controversial': 1, 'story': 1, 'conditional': 1, 'leaving': 1, 'disarray': 1, 'investment': 1, 'added': 1, 'regularly': 1, 'police': 1, 'August': 1, 'undoubtedly': 1, 'fierce': 1, 'heralded': 1, 'leading': 1, 'Forces': 1, 'atmosphere': 1, 'media': 1, 'number': 1, 'protests': 1, 'scale': 1, 'age': 1, 'nuclear': 1, 'industries': 1, 'movements': 1, 'attacks': 1, 'major': 1, 'denouncing': 1, 'Oppose': 1, 'office': 1, 'forming': 1, 'successful': 1, 'served': 1, 'limelight': 1, 'faction': 1, 'worsening': 1, 'bloodshed': 1, 'killing': 1, 'grandmaster': 1, 'worth': 1, 'warning': 1, 'played': 1, 'Initially': 1, 'military': 1, 'eloquent': 1, '1998': 1, 'reunite': 1, 'futile': 1, 'left': 1, 'critics': 1, 'chair': 1, 'rose': 1, 'deputy': 1, '1990s': 1, 'Republican': 1, 'prime': 1, 'alleged': 1, 'politicians': 1, 'Solidarnost': 1, '1999': 1, 'Any': 1, 'Navalny': 1, 'undermined': 1, 'eventually': 1, 'English': 1, 'influential': 1, 'rewarded': 1, 'life': 1, 'bristling': 1, '10': 1, 'catastrophe': 1, 'penguins': 1, 'hamsters': 1, 'total': 1, 'figure': 1, 'The': 1, 'Sochi': 1, 'minor': 1, 'People': 1, 'Possible': 1, 'minister': 1, 'reformer': 1, 'speculated': 1, 'rifts': 1, 'December': 1, 'government': 1, 'showcase': 1, 'rallies': 1, 'evolved': 1, 'charge': 1, 'needed': 1, 'future': 1, 'Continue': 1, 'An': 1, 'Union': 1, 'enter': 1, 'unsuccessfully': 1, 'post': 1, 'quickly': 1, 'resigned': 1, 'father': 1, 'lost': 1, 'Anatoly': 1, 'actions': 1, 'surrounding': 1, '5': 1, 'situation': 1, 'move': 1, 'large': 1, 'emerged': 1, 'cost': 1, 'inevitable': 1, 'Yegor': 1, 'regional': 1, '1997': 1, 'observers': 1, 'Sunday': 1, 'preparations': 1, 'co': 1, 'Garry': 1, 'job': 1, 'reading': 1, 'ambitions': 1, 'figures': 1, 'fluent': 1, 'Games': 1, 'calling': 1, 'prominence': 1, 'successor': 1, 'chess': 1, 'Alexei': 1, 'Still': 1, 'fallen': 1, 'presidency': 1, 'loyalty': 1, 'pursued': 1, 'threshold': 1, 'support': 1, 'business': 1, 'political': 1, 'young': 1, 'death': 1, 'career': 1, 'corruption': 1, 'environmentalist': 1, 'Right': 1, 'rally': 1, 'beginning': 1, 'governor': 1, 'Political': 1, 'stood': 1, 'Along': 1, '1989': 1, 'dead': 1, 'shocked': 1, 'Chubais': 1, 'foreign': 1, 'leader': 1, 'reach': 1, 'mainstream': 1, 'moderately': 1, 'grooming': 1, 'savvy': 1, 'Gaidar': 1, 'reform': 1, '55': 1, 'attempts': 1, 'face': 1, 'gaining': 1, 'Related': 1, 'transgressions': 1, '2014': 1, 'fractured': 1, 'making': 1, 'hatred': 1, 'regret': 1})*/
		// 0.618
	}
}

