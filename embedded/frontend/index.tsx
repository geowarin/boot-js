import * as React from "react";
import {render} from "react-dom";
import wretch from "wretch";
import {useAsync} from "react-async-hook";
import {Link, Route} from "wouter";

const RestPage = () => {
  const {error, result, loading} = useAsync<{ message: string }>(async () => wretch().url("/api/message").get().text(), []);
  if (loading) return <div>loading...</div>;
  if (error) return <div>failed to load {error.message}</div>;

  return (
    <>
      <p>Message from server: {result} !!</p>
      <Link to="/other">Go to the other page</Link>
    </>
  );
};

const OtherPage = () => {
  return (
    <>
      <h2>Ho hai!</h2>
      <Link to="/">Go to the home page</Link>
    </>
  )
};

const App = () => {
  return (
    <>
      <Route path="/" component={RestPage}/>
      <Route path="/other" component={OtherPage}/>
    </>
  )
};

const rootElement = document.getElementById("root");
render(<App/>, rootElement);
