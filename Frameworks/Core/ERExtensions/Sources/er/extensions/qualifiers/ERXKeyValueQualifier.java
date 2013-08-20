package er.extensions.qualifiers;

import com.webobjects.eocontrol.EOKeyValueQualifier;
import com.webobjects.eocontrol.EOQualifier;
import com.webobjects.eocontrol.EOQualifierVariable;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSKeyValueCodingAdditions;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSSelector;
import com.webobjects.foundation._NSStringUtilities;

import er.extensions.eof.ERXQ;
import er.extensions.foundation.ERXArrayUtilities;
import er.extensions.foundation.ERXProperties;

/**
 * ERXKeyValueQualifier is a chainable extension of EOKeyValueQualifier.
 * 
 * @author mschrag
 */
public class ERXKeyValueQualifier extends EOKeyValueQualifier implements IERXChainableQualifier {
	
	// Lazy static initialization
	private static class PROPERTIES {																 
		static boolean shouldEvaluateManyToManyValueObject = ERXProperties.booleanForKeyWithDefault("er.extensions.ERXKeyValueQualifier.manyToMany", false);
	} 
	
	public ERXKeyValueQualifier(String key, NSSelector selector, Object value) {
		super(key, selector, value);
		if (key == null) {
			throw new IllegalArgumentException("A KeyQualifierQualifier must have a key.");
		}
		if (selector == null) {
			throw new IllegalArgumentException("A KeyQualifierQualifier must have a selector.");
		}
	}
	
	@Override
	public boolean evaluateWithObject(Object object){  
		if(super.evaluateWithObject(object)){
			return true;
		}

		if(PROPERTIES.shouldEvaluateManyToManyValueObject){
			Object objectValue = NSKeyValueCodingAdditions.Utility.valueForKeyPath(object, _key);
			if (objectValue instanceof NSMutableArray<?>) {
				for (Object value : (NSMutableArray<?>) objectValue) {
					NSDictionary<String, Object> newObject = new NSDictionary<String, Object>(value, _key);
					if(evaluateWithObject(newObject)){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public ERXAndQualifier and(EOQualifier... qualifiers) {
		return ERXChainedQualifierUtils.and(this, qualifiers);
	}

	public ERXNotQualifier not() {
		return ERXChainedQualifierUtils.not(this);
	}

	public ERXOrQualifier or(EOQualifier... qualifiers) {
		return ERXChainedQualifierUtils.or(this, qualifiers);
	}

	public void filter(NSMutableArray<?> array) {
		ERXQ.filter(array, this);
	}

	public <T> NSArray<T> filtered(NSArray<T> array) {
		return ERXQ.filtered(array, this);
	}

	public <T> T first(NSArray<T> array) {
		return ERXQ.first(array, this);
	}

	public <T> T one(NSArray<T> array) {
		return ERXQ.one(array, this);
	}

	public <T> T requiredOne(NSArray<T> array) {
		return ERXQ.requiredOne(array, this);
	}
}
