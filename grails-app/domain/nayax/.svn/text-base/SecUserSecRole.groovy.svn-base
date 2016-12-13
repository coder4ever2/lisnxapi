package nayax

import org.apache.commons.lang.builder.HashCodeBuilder

class SecUserSecRole implements Serializable {

	NayaxUser nayaxUser
	SecRole secRole

	boolean equals(other) {
		if (!(other instanceof SecUserSecRole)) {
			return false
		}

		other.nayaxUser?.id == nayaxUser?.id &&
			other.secRole?.id == secRole?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (nayaxUser) builder.append(nayaxUser.id)
		if (secRole) builder.append(secRole.id)
		builder.toHashCode()
	}

	static SecUserSecRole get(long nayaxUserId, long secRoleId) {
		find 'from SecUserSecRole where nayaxUser.id=:nayaxUserId and secRole.id=:secRoleId',
			[nayaxUserId: nayaxUserId, secRoleId: secRoleId]
	}

	static SecUserSecRole create(NayaxUser nayaxUser, SecRole secRole, boolean flush = false) {
		new SecUserSecRole(nayaxUser: nayaxUser, secRole: secRole).save(flush: flush, insert: true)
	}

	static boolean remove(NayaxUser nayaxUser, SecRole secRole, boolean flush = false) {
		SecUserSecRole instance = SecUserSecRole.findByNayaxUserAndSecRole(nayaxUser, secRole)
		instance ? instance.delete(flush: flush) : false
	}

	static void removeAll(NayaxUser nayaxUser) {
		executeUpdate 'DELETE FROM SecUserSecRole WHERE nayaxUser=:nayaxUser', [nayaxUser: nayaxUser]
	}

	static void removeAll(SecRole secRole) {
		executeUpdate 'DELETE FROM SecUserSecRole WHERE secRole=:secRole', [secRole: secRole]
	}

	static mapping = {
		id composite: ['secRole', 'nayaxUser']
        nayaxUser column:'`sec_user_id`'
		version false
	}
}
