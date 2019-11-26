package dm.uporov.machete.exception

class SeveralScopesUseTheSameIdException(id: Int) : RuntimeException("Several scopes use id $id")