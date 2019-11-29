package dm.uporov.machete.exception_legacy

class SeveralScopesUseTheSameIdException(id: Int) : RuntimeException("Several scopes use id $id")