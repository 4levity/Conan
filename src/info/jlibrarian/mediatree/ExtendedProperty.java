package info.jlibrarian.mediatree;

import info.jlibrarian.propertytree.Property;

public class ExtendedProperty implements Property {
	String name;
	Property superType;
	
	public ExtendedProperty(String name,Property superType) {
		super();
		this.name = name;
		this.superType = superType;
	}

	@Override
	public Class<?> getDataType() {
		return superType.getClass();
	}

	@Override
	public boolean isTypeOf(Property p) {
		if(p==null)
			return true;
		if(equals(p))
			return true;
		return this.superType.isTypeOf(p);
	}

	@Override
	public boolean getIsUniqueAttribute() {
		return false;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((superType == null) ? 0 : superType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExtendedProperty other = (ExtendedProperty) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (superType != other.superType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public String getDescription() {
		return "(Extended property)";
	}

}
