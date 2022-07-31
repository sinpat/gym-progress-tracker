package web

object HTML {
  def htmlRoot(head: String, body: String): String =
    s"<html><head>$head</head><body>$body</body></html>"
}
