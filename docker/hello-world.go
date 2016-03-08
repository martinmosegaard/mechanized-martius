package main

import (
	"fmt"
	"net/http"
)

func homeHandler() http.Handler {
	return http.HandlerFunc(func(writer http.ResponseWriter, request *http.Request) {
		fmt.Fprintf(writer, "Hello World!")
	})
}

func main() {
	http.Handle("/", homeHandler())
	http.ListenAndServe(":80", nil)
}
