package com.github.alex1304.jdashevents.customcomponents;

import com.github.alex1304.jdash.component.GDComponent;

/**
 * A component that encloses two components of same type.
 * They are supposed to represent the same component but in a different state
 *
 * @author Alex1304
 */
public class GDUpdatedComponent<T extends GDComponent> implements GDComponent {

	private T beforeUpdate, afterUpdate;

	/**
	 * @param beforeUpdate
	 *            - the component before it was updated
	 * @param afterUpdate
	 *            - the same component after it was updated
	 */
	public GDUpdatedComponent(T beforeUpdate, T afterUpdate) {
		this.beforeUpdate = beforeUpdate;
		this.afterUpdate = afterUpdate;
	}

	/**
	 * Gets the component before it was updated
	 *
	 * @return T
	 */
	public T getBeforeUpdate() {
		return beforeUpdate;
	}

	/**
	 * Gets the same component after it was updated
	 *
	 * @return T
	 */
	public T getAfterUpdate() {
		return afterUpdate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((afterUpdate == null) ? 0 : afterUpdate.hashCode());
		result = prime * result + ((beforeUpdate == null) ? 0 : beforeUpdate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GDUpdatedComponent))
			return false;
		GDUpdatedComponent<?> other = (GDUpdatedComponent<?>) obj;
		if (afterUpdate == null) {
			if (other.afterUpdate != null)
				return false;
		} else if (!afterUpdate.equals(other.afterUpdate))
			return false;
		if (beforeUpdate == null) {
			if (other.beforeUpdate != null)
				return false;
		} else if (!beforeUpdate.equals(other.beforeUpdate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GDUpdatedComponent [beforeUpdate=" + beforeUpdate + ", afterUpdate=" + afterUpdate + "]";
	}

}
