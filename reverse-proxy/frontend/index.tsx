import * as React from "react";
import {render} from "react-dom";
import wretch from "wretch";
import {useAsync} from "react-async-hook";

const App = () => {
  const state = useAsync<{ message: string }>(async () => wretch().url("/api/message").get().json(), []);

  if (state.loading) return <div>loading...</div>;
  if (state.error) return <div>failed to load</div>;
  return <div>Message from server: {state.result?.message} !!</div>;
};

const rootElement = document.getElementById("root");
render(<App/>, rootElement);
